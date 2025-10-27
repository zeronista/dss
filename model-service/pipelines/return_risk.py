"""
Return Risk Prediction Pipeline - Prescriptive Decision Support
Implements risk scoring, expected profit calculation, and optimal threshold (τ*) optimization
"""

import numpy as np
import pandas as pd
from typing import Dict, Any, List, Tuple, Optional


class ReturnRiskPipeline:
    """
    Return-Risk Gatekeeping Policy - Prescriptive Decisions
    
    Features:
    1. Risk scoring (0-100 scale)
    2. Expected profit calculation per threshold
    3. Optimal threshold (τ*) recommendation
    4. What-if simulation for policy decisions
    """
    
    def __init__(self, db_connection=None):
        """
        Initialize the return risk pipeline
        
        Args:
            db_connection: Database connection for fetching historical data
        """
        self.db = db_connection
        
        # Model coefficients (can be learned from data)
        self.alpha = 40.0  # Weight for customer return rate
        self.beta = 35.0   # Weight for SKU return rate
        self.gamma = 15.0  # Weight for first-time customer (inverse)
        self.delta = 0.05  # Weight for order value (inverse)
        
        # Default business parameters
        self.default_params = {
            'return_processing_cost': 15.0,
            'shipping_cost': 5.0,
            'cogs_ratio': 0.6,  # 60% of revenue
            'conversion_rate_impact': 0.2  # 20% loss when blocking COD
        }
    
    def calculate_risk_score(self, features: Dict[str, Any]) -> float:
        """
        Calculate return risk score (0-100 scale)
        
        Score = α·Customer_Return_Rate + β·SKU_Return_Rate + γ·Is_First_Time − δ·Order_Value
        
        Args:
            features: Dictionary containing:
                - customer_return_rate: 0-1 (percentage as decimal)
                - sku_return_rate: 0-1
                - is_first_time_customer: boolean
                - order_value: numeric
        
        Returns:
            Risk score in range [0, 100]
        """
        customer_rate = features.get('customer_return_rate', 0.0)
        sku_rate = features.get('sku_return_rate', 0.0)
        is_first_time = 1 if features.get('is_first_time_customer', False) else 0
        order_value = features.get('order_value', 0.0)
        
        # Calculate raw score
        score = (
            self.alpha * customer_rate +
            self.beta * sku_rate +
            self.gamma * is_first_time -
            self.delta * order_value
        )
        
        # Normalize to 0-100 range
        score = np.clip(score, 0, 100)
        
        return round(score, 2)
    
    def calculate_expected_profit(
        self,
        revenue: float,
        risk_score: float,
        threshold: float,
        params: Dict[str, float]
    ) -> Tuple[float, float]:
        """
        Calculate expected profit for approve vs. block scenarios
        
        Args:
            revenue: Order revenue
            risk_score: Return risk score (0-100)
            threshold: Decision threshold τ
            params: Business parameters (costs, impact)
        
        Returns:
            Tuple of (profit_if_approved, profit_if_blocked)
        """
        cogs = revenue * params.get('cogs_ratio', self.default_params['cogs_ratio'])
        shipping = params.get('shipping_cost', self.default_params['shipping_cost'])
        return_cost = params.get('return_processing_cost', self.default_params['return_processing_cost'])
        conversion_impact = params.get('conversion_rate_impact', self.default_params['conversion_rate_impact'])
        
        # Risk probability (score/100)
        risk_prob = risk_score / 100.0
        
        # Scenario 1: Approve (let it go through)
        profit_approved = (revenue - cogs - shipping) - (risk_prob * return_cost)
        
        # Scenario 2: Block/Restrict (apply gatekeeping policy)
        # Some orders won't convert due to stricter policy
        profit_blocked = (revenue - cogs - shipping) * (1 - conversion_impact)
        
        return round(profit_approved, 2), round(profit_blocked, 2)
    
    def predict_single_order(
        self,
        order_data: Dict[str, Any],
        threshold: Optional[float] = None,
        params: Optional[Dict[str, float]] = None
    ) -> Dict[str, Any]:
        """
        Predict return risk and recommend action for a single order
        
        Args:
            order_data: Order information
            threshold: Decision threshold (if None, use default 75)
            params: Business parameters
        
        Returns:
            Prediction result with risk score and recommendation
        """
        if params is None:
            params = self.default_params.copy()
        
        if threshold is None:
            threshold = 75.0
        
        # Extract features
        features = {
            'customer_return_rate': order_data.get('customer_return_rate', 0.0),
            'sku_return_rate': order_data.get('sku_return_rate', 0.0),
            'is_first_time_customer': order_data.get('is_first_time_customer', False),
            'order_value': order_data.get('order_value', 0.0)
        }
        
        # Calculate risk score
        risk_score = self.calculate_risk_score(features)
        
        # Determine risk level
        if risk_score < 50:
            risk_level = 'LOW'
        elif risk_score < 75:
            risk_level = 'MEDIUM'
        else:
            risk_level = 'HIGH'
        
        # Calculate expected profits
        revenue = order_data.get('order_value', 0.0)
        profit_approved, profit_blocked = self.calculate_expected_profit(
            revenue, risk_score, threshold, params
        )
        
        # Make recommendation based on threshold
        if risk_score < threshold:
            recommended_action = 'APPROVE'
            action_reason = f'Risk score ({risk_score}) below threshold ({threshold})'
        else:
            # Choose action based on profit comparison
            if profit_blocked > profit_approved:
                recommended_action = 'BLOCK_COD'
                action_reason = f'High risk ({risk_score}) - Expected profit higher if blocked'
            else:
                recommended_action = 'REQUIRE_PREPAY'
                action_reason = f'High risk ({risk_score}) - Require prepayment to mitigate'
        
        result = {
            'orderId': order_data.get('order_id', ''),
            'riskScore': risk_score,
            'riskLevel': risk_level,
            'recommendedAction': recommended_action,
            'actionReason': action_reason,
            'expectedProfitIfApproved': profit_approved,
            'expectedProfitIfBlocked': profit_blocked,
            'profitDifference': round(profit_blocked - profit_approved, 2),
            'features': features,
            'thresholdUsed': threshold
        }
        
        return result
    
    def simulate_policy(
        self,
        orders_data: List[Dict[str, Any]],
        threshold: float,
        params: Optional[Dict[str, float]] = None
    ) -> Dict[str, Any]:
        """
        Simulate policy impact on a batch of orders
        
        Args:
            orders_data: List of order dictionaries
            threshold: Decision threshold τ
            params: Business parameters
        
        Returns:
            Simulation results with total expected profit and metrics
        """
        if params is None:
            params = self.default_params.copy()
        
        total_profit = 0.0
        orders_impacted = 0
        revenue_at_risk = 0.0
        
        for order in orders_data:
            # Get risk score
            features = {
                'customer_return_rate': order.get('customer_return_rate', 0.0),
                'sku_return_rate': order.get('sku_return_rate', 0.0),
                'is_first_time_customer': order.get('is_first_time_customer', False),
                'order_value': order.get('order_value', 0.0)
            }
            
            risk_score = self.calculate_risk_score(features)
            revenue = order.get('order_value', 0.0)
            
            # Calculate expected profit
            profit_approved, profit_blocked = self.calculate_expected_profit(
                revenue, risk_score, threshold, params
            )
            
            # Apply policy decision
            if risk_score < threshold:
                # Approve - use approved profit
                total_profit += profit_approved
            else:
                # Block/Restrict - use blocked profit
                total_profit += profit_blocked
                orders_impacted += 1
                revenue_at_risk += revenue
        
        total_orders = len(orders_data)
        orders_impacted_pct = (orders_impacted / total_orders * 100) if total_orders > 0 else 0
        
        return {
            'threshold': threshold,
            'totalExpectedProfit': round(total_profit, 2),
            'totalOrders': total_orders,
            'ordersImpacted': orders_impacted,
            'ordersImpactedPct': round(orders_impacted_pct, 2),
            'revenueAtRisk': round(revenue_at_risk, 2)
        }
    
    def find_optimal_threshold(
        self,
        orders_data: List[Dict[str, Any]],
        params: Optional[Dict[str, float]] = None,
        threshold_range: Tuple[float, float] = (0, 100),
        step: float = 1.0
    ) -> Dict[str, Any]:
        """
        Find optimal threshold τ* that maximizes total expected profit
        
        Args:
            orders_data: List of order dictionaries
            params: Business parameters
            threshold_range: (min, max) threshold to search
            step: Step size for threshold sweep
        
        Returns:
            Optimal threshold and profit curve data
        """
        if params is None:
            params = self.default_params.copy()
        
        thresholds = np.arange(threshold_range[0], threshold_range[1] + step, step)
        profit_curve = []
        max_profit = float('-inf')
        optimal_threshold = 0.0
        
        # Sweep through thresholds
        for tau in thresholds:
            result = self.simulate_policy(orders_data, float(tau), params)
            
            profit_curve.append({
                'threshold': float(tau),
                'profit': result['totalExpectedProfit'],
                'ordersImpacted': result['ordersImpacted'],
                'revenueImpacted': result['revenueAtRisk']
            })
            
            if result['totalExpectedProfit'] > max_profit:
                max_profit = result['totalExpectedProfit']
                optimal_threshold = float(tau)
        
        # Generate policy rules based on optimal threshold
        policy_rules = self._generate_policy_rules(optimal_threshold)
        
        # Calculate baseline (threshold = 0, approve all)
        baseline = self.simulate_policy(orders_data, 0.0, params)
        profit_gain = max_profit - baseline['totalExpectedProfit']
        
        return {
            'optimalThreshold': optimal_threshold,
            'maxExpectedProfit': round(max_profit, 2),
            'profitGainVsBaseline': round(profit_gain, 2),
            'profitCurve': profit_curve,
            'policyRules': policy_rules,
            'recommendation': f'Set threshold to {optimal_threshold:.0f} to protect ${profit_gain:,.2f} in profit',
            'sensitivityNote': self._analyze_sensitivity(profit_curve, optimal_threshold)
        }
    
    def _generate_policy_rules(self, threshold: float) -> List[Dict[str, str]]:
        """Generate policy decision matrix based on threshold"""
        rules = []
        
        if threshold > 0:
            rules.append({
                'scoreRange': f'0 - {threshold:.0f}',
                'action': 'APPROVE',
                'description': 'Low risk - Approve with standard policy'
            })
        
        if threshold < 100:
            mid_threshold = (threshold + 100) / 2
            rules.append({
                'scoreRange': f'{threshold:.0f} - {mid_threshold:.0f}',
                'action': 'REQUIRE_PREPAY',
                'description': 'Medium-High risk - Require prepayment'
            })
            rules.append({
                'scoreRange': f'{mid_threshold:.0f} - 100',
                'action': 'BLOCK_COD',
                'description': 'Very High risk - Block COD, cash only'
            })
        
        return rules
    
    def _analyze_sensitivity(self, profit_curve: List[Dict], optimal_tau: float) -> str:
        """Analyze sensitivity of optimal threshold"""
        # Find profit values near optimal
        optimal_profit = next((p['profit'] for p in profit_curve if p['threshold'] == optimal_tau), 0)
        
        # Check if profit plateau exists
        plateau_thresholds = [
            p['threshold'] for p in profit_curve
            if abs(p['profit'] - optimal_profit) / optimal_profit < 0.02  # Within 2%
        ]
        
        if len(plateau_thresholds) > 5:
            return f'Profit relatively stable around τ* (range: {min(plateau_thresholds):.0f}-{max(plateau_thresholds):.0f})'
        else:
            return f'Profit sensitive to threshold - recommend staying close to τ* = {optimal_tau:.0f}'
    
    def train(self, training_data: pd.DataFrame):
        """
        Train the return risk model using historical data
        
        Args:
            training_data: DataFrame with columns:
                - customer_return_rate
                - sku_return_rate
                - is_first_time_customer
                - order_value
                - is_return (target)
        """
        # This is a placeholder for model training
        # In production, you would use logistic regression or more advanced ML models
        # to learn optimal weights (alpha, beta, gamma, delta)
        
        # For now, using rule-based weights
        pass

