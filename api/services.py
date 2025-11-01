# services.py
# Business logic services for DSS API
# Author: GitHub Copilot

import pandas as pd
import numpy as np
from datetime import datetime, date
from typing import Dict, Any, List, Tuple, Optional
from dateutil.relativedelta import relativedelta

from statsmodels.tsa.statespace.sarimax import SARIMAX
from sklearn.preprocessing import StandardScaler
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score
from mlxtend.frequent_patterns import apriori, association_rules

import pycountry

class DataService:
    """Service for data loading, cleaning and basic operations"""
    
    @staticmethod
    def clean_data(df: pd.DataFrame) -> pd.DataFrame:
        """Clean and prepare raw data"""
        # Convert date
        df['InvoiceDate'] = pd.to_datetime(df['InvoiceDate'], errors='coerce')
        df = df.dropna(subset=['InvoiceDate'])
        
        # Drop missing CustomerID
        if 'CustomerID' in df.columns:
            df = df.dropna(subset=['CustomerID']).copy()
            df['CustomerID'] = df['CustomerID'].astype(int)
        
        # Ensure numeric
        df['Quantity'] = pd.to_numeric(df['Quantity'], errors='coerce').fillna(0)
        df['UnitPrice'] = pd.to_numeric(df['UnitPrice'], errors='coerce').fillna(0.0)
        
        # Remove cancelled orders
        if 'InvoiceNo' in df.columns:
            df = df[~df['InvoiceNo'].astype(str).str.startswith('C', na=False)].copy()
        
        # Positive sales only
        df = df[(df['Quantity'] > 0) & (df['UnitPrice'] > 0)].copy()
        
        # Calculate revenue
        df['Revenue'] = df['Quantity'] * df['UnitPrice']
        
        # Date helpers
        df['InvoiceDateDate'] = df['InvoiceDate'].dt.date
        df['YearMonth'] = df['InvoiceDate'].dt.to_period('M')
        
        return df
    
    @staticmethod
    def filter_by_date(df: pd.DataFrame, start_date: date, end_date: date) -> pd.DataFrame:
        """Filter dataframe by date range"""
        mask = (
            (df['InvoiceDate'] >= pd.to_datetime(start_date)) & 
            (df['InvoiceDate'] <= pd.to_datetime(end_date) + pd.Timedelta(days=1) - pd.Timedelta(seconds=1))
        )
        return df.loc[mask].copy()
    
    @staticmethod
    def get_basic_stats(df: pd.DataFrame) -> Dict[str, Any]:
        """Calculate basic statistics"""
        return {
            "total_revenue": float(df['Revenue'].sum()),
            "total_transactions": int(df['InvoiceNo'].nunique()) if 'InvoiceNo' in df.columns else 0,
            "total_customers": int(df['CustomerID'].nunique()) if 'CustomerID' in df.columns else 0,
            "total_products": int(df['StockCode'].nunique()) if 'StockCode' in df.columns else 0,
            "avg_transaction_value": float(df.groupby('InvoiceNo')['Revenue'].sum().mean()) if 'InvoiceNo' in df.columns else 0,
            "date_range": {
                "min": df['InvoiceDate'].min().isoformat(),
                "max": df['InvoiceDate'].max().isoformat()
            }
        }
    
    @staticmethod
    def calculate_overview_metrics(df: pd.DataFrame) -> Dict[str, Any]:
        """Calculate comprehensive overview metrics"""
        total_revenue = float(df['Revenue'].sum())
        total_customers = int(df['CustomerID'].nunique()) if 'CustomerID' in df.columns else 0
        total_orders = int(df['InvoiceNo'].nunique()) if 'InvoiceNo' in df.columns else 0
        avg_order_value = total_revenue / total_orders if total_orders > 0 else 0
        
        # Top products
        top_products = (
            df.groupby('Description', dropna=True)['Quantity']
            .sum()
            .sort_values(ascending=False)
            .head(10)
            .reset_index()
            .to_dict('records')
        )
        
        # Top customers
        top_customers = (
            df.groupby('CustomerID')['Revenue']
            .sum()
            .sort_values(ascending=False)
            .head(10)
            .reset_index()
            .to_dict('records')
        )
        
        # Monthly revenue
        monthly_revenue = (
            df.set_index('InvoiceDate')
            .resample('MS')['Revenue']
            .sum()
            .reset_index()
        )
        monthly_revenue['InvoiceDate'] = monthly_revenue['InvoiceDate'].dt.strftime('%Y-%m-%d')
        monthly_data = monthly_revenue.to_dict('records')
        
        # Revenue by country
        if 'Country' in df.columns:
            country_revenue = (
                df.groupby('Country')['Revenue']
                .sum()
                .sort_values(ascending=False)
                .head(10)
                .reset_index()
                .to_dict('records')
            )
        else:
            country_revenue = []
        
        # Generate insights
        insights = DataService._generate_insights(df, monthly_revenue)
        
        return {
            "total_revenue": total_revenue,
            "total_customers": total_customers,
            "total_orders": total_orders,
            "avg_order_value": avg_order_value,
            "top_products": top_products,
            "top_customers": top_customers,
            "monthly_revenue": monthly_data,
            "revenue_by_country": country_revenue,
            "insights": insights
        }
    
    @staticmethod
    def _generate_insights(df: pd.DataFrame, monthly_df: pd.DataFrame) -> List[str]:
        """Generate business insights"""
        insights = []
        
        # Top product insight
        top_prod = df.groupby('Description')['Quantity'].sum().sort_values(ascending=False).head(1)
        if len(top_prod):
            p_name = top_prod.index[0]
            p_qty = top_prod.values[0]
            qty_total = df['Quantity'].sum()
            share = (p_qty / qty_total * 100) if qty_total > 0 else 0
            insights.append(f"Sản phẩm chủ lực: {p_name} chiếm {share:.1f}% tổng số lượng bán ra")
        
        # Top country insight
        if 'Country' in df.columns:
            top_cty = df.groupby('Country')['Revenue'].sum().sort_values(ascending=False).head(1)
            if len(top_cty):
                c_name = top_cty.index[0]
                c_rev = top_cty.values[0]
                rev_total = df['Revenue'].sum()
                share_r = (c_rev / rev_total * 100) if rev_total > 0 else 0
                insights.append(f"Thị trường lớn nhất: {c_name} đóng góp {share_r:.1f}% doanh thu")
        
        # Month-over-month trend
        if len(monthly_df) >= 2:
            last = monthly_df.iloc[-1]['Revenue']
            prev = monthly_df.iloc[-2]['Revenue']
            if prev > 0:
                change = (last - prev) / prev * 100
                direction = "tăng" if change >= 0 else "giảm"
                insights.append(f"Doanh thu tháng gần nhất {direction} {abs(change):.1f}% so với tháng trước")
        
        return insights


class ForecastService:
    """Service for revenue forecasting using SARIMAX"""
    
    @staticmethod
    def forecast_revenue(
        df: pd.DataFrame, 
        periods: int = 3, 
        confidence_level: float = 0.90
    ) -> Dict[str, Any]:
        """Generate revenue forecast using SARIMAX model"""
        # Aggregate to monthly
        monthly = df.set_index('InvoiceDate').resample('MS')['Revenue'].sum()
        monthly = monthly.asfreq('MS').fillna(0)
        
        # Fit SARIMAX model
        try:
            model = SARIMAX(
                monthly, 
                order=(1,1,1), 
                seasonal_order=(1,0,1,12),
                enforce_stationarity=False,
                enforce_invertibility=False
            )
            results = model.fit(disp=False)
            
            # Forecast
            forecast = results.get_forecast(steps=periods)
            predicted_mean = forecast.predicted_mean
            conf_int = forecast.conf_int(alpha=1-confidence_level)
            
            # Model metrics
            aic = float(results.aic)
            bic = float(results.bic)
            
        except Exception as e:
            # Fallback to naive forecast
            last_value = float(monthly.iloc[-1]) if len(monthly) else 0.0
            forecast_dates = pd.date_range(
                monthly.index[-1] + pd.offsets.MonthBegin(), 
                periods=periods, 
                freq='MS'
            ) if len(monthly) else pd.date_range(pd.Timestamp.today().normalize(), periods=periods, freq='MS')
            
            predicted_mean = pd.Series([last_value] * periods, index=forecast_dates)
            conf_int = pd.DataFrame({
                'lower Revenue': [last_value * 0.9] * periods,
                'upper Revenue': [last_value * 1.1] * periods
            }, index=forecast_dates)
            
            aic = None
            bic = None
        
        # Format historical data
        historical_data = [
            {
                "date": date.strftime('%Y-%m-%d'),
                "revenue": float(value)
            }
            for date, value in monthly.items()
        ]
        
        # Format forecast data
        forecast_data = []
        for i, date in enumerate(predicted_mean.index):
            forecast_data.append({
                "date": date.strftime('%Y-%m-%d'),
                "predicted_value": float(predicted_mean.iloc[i]),
                "lower_bound": float(conf_int.iloc[i, 0]),
                "upper_bound": float(conf_int.iloc[i, 1])
            })
        
        # Generate insights
        insights = ForecastService._generate_forecast_insights(monthly, predicted_mean)
        
        return {
            "historical_data": historical_data,
            "forecast_data": forecast_data,
            "model_info": {
                "model_type": "SARIMAX",
                "order": "(1,1,1)",
                "seasonal_order": "(1,0,1,12)",
                "confidence_level": confidence_level
            },
            "insights": insights,
            "metrics": {
                "aic": aic,
                "bic": bic
            }
        }
    
    @staticmethod
    def _generate_forecast_insights(historical: pd.Series, forecast: pd.Series) -> List[str]:
        """Generate forecast insights"""
        insights = []
        
        # Calculate trend
        if len(historical) >= 3:
            recent_avg = historical.iloc[-3:].mean()
        else:
            recent_avg = historical.mean() if len(historical) > 0 else 0
        
        forecast_avg = forecast.mean() if len(forecast) > 0 else 0
        
        if recent_avg > 0:
            change_pct = ((forecast_avg - recent_avg) / recent_avg) * 100
        else:
            change_pct = 0
        
        if change_pct > 10:
            insights.append(f"Dự báo tăng trưởng mạnh ({change_pct:.1f}%): Hãy tăng tồn kho và chuẩn bị nguồn lực")
        elif change_pct < -10:
            insights.append(f"Dự báo suy giảm ({change_pct:.1f}%): Cân nhắc khuyến mãi để kích thích sức mua")
        else:
            insights.append(f"Dự báo ổn định ({change_pct:.1f}%): Duy trì mức tồn kho hiện tại")
        
        return insights


class RFMService:
    """Service for RFM (Recency, Frequency, Monetary) analysis"""
    
    @staticmethod
    def calculate_rfm(df: pd.DataFrame, reference_date: Optional[date] = None) -> Dict[str, Any]:
        """Calculate RFM metrics for customers"""
        if reference_date is None:
            ref_date = pd.to_datetime(df['InvoiceDate']).max()
        else:
            ref_date = pd.to_datetime(reference_date)
        
        # Calculate RFM
        rfm = df.groupby('CustomerID').agg(
            Recency=('InvoiceDate', lambda x: (ref_date - x.max()).days),
            Frequency=('InvoiceNo', pd.Series.nunique),
            Monetary=('Revenue', 'sum')
        ).reset_index()
        
        # Calculate RFM scores (1-5 scale)
        rfm['R_Score'] = pd.qcut(rfm['Recency'], q=5, labels=[5,4,3,2,1], duplicates='drop')
        rfm['F_Score'] = pd.qcut(rfm['Frequency'].rank(method='first'), q=5, labels=[1,2,3,4,5], duplicates='drop')
        rfm['M_Score'] = pd.qcut(rfm['Monetary'], q=5, labels=[1,2,3,4,5], duplicates='drop')
        
        rfm['RFM_Score'] = (
            rfm['R_Score'].astype(str) + 
            rfm['F_Score'].astype(str) + 
            rfm['M_Score'].astype(str)
        )
        
        # Summary statistics
        summary_stats = {
            "total_customers": int(len(rfm)),
            "avg_recency": float(rfm['Recency'].mean()),
            "avg_frequency": float(rfm['Frequency'].mean()),
            "avg_monetary": float(rfm['Monetary'].mean()),
            "median_recency": float(rfm['Recency'].median()),
            "median_frequency": float(rfm['Frequency'].median()),
            "median_monetary": float(rfm['Monetary'].median())
        }
        
        # Distribution
        distribution = {
            "recency": rfm['Recency'].value_counts(bins=10).sort_index().tolist(),
            "frequency": rfm['Frequency'].value_counts(bins=10).sort_index().tolist(),
            "monetary": rfm['Monetary'].value_counts(bins=10).sort_index().tolist()
        }
        
        # Convert to dict
        customers = rfm.to_dict('records')
        
        return {
            "customers": customers,
            "summary_stats": summary_stats,
            "distribution": distribution
        }


class SegmentationService:
    """Service for customer segmentation using K-Means clustering"""
    
    @staticmethod
    def segment_customers(
        df: pd.DataFrame, 
        n_clusters: int = 4, 
        random_state: int = 42
    ) -> Dict[str, Any]:
        """Perform customer segmentation"""
        # Calculate RFM first
        ref_date = pd.to_datetime(df['InvoiceDate']).max()
        rfm = df.groupby('CustomerID').agg(
            Recency=('InvoiceDate', lambda x: (ref_date - x.max()).days),
            Frequency=('InvoiceNo', pd.Series.nunique),
            Monetary=('Revenue', 'sum')
        ).reset_index()
        
        # Standardize features
        scaler = StandardScaler()
        X = scaler.fit_transform(rfm[['Recency', 'Frequency', 'Monetary']])
        
        # K-Means clustering
        kmeans = KMeans(n_clusters=n_clusters, n_init=10, random_state=random_state)
        rfm['Segment'] = kmeans.fit_predict(X)
        
        # Calculate silhouette score
        try:
            sil_score = float(silhouette_score(X, rfm['Segment']))
        except:
            sil_score = None
        
        # Assign segment names
        quantiles = rfm[['Recency', 'Frequency', 'Monetary']].quantile([0.25, 0.5, 0.75])
        rfm['SegmentName'] = rfm.apply(
            lambda row: SegmentationService._assign_segment_name(row, quantiles), 
            axis=1
        )
        
        # Segment summary
        segment_summary = []
        for seg_name in rfm['SegmentName'].unique():
            seg_data = rfm[rfm['SegmentName'] == seg_name]
            
            segment_info = {
                "segment_id": int(seg_data['Segment'].iloc[0]),
                "segment_name": seg_name,
                "customer_count": int(len(seg_data)),
                "total_value": float(seg_data['Monetary'].sum()),
                "avg_recency": float(seg_data['Recency'].mean()),
                "avg_frequency": float(seg_data['Frequency'].mean()),
                "avg_monetary": float(seg_data['Monetary'].mean()),
                "characteristics": SegmentationService._get_segment_characteristics(
                    seg_name, 
                    float(seg_data['Recency'].mean()),
                    float(seg_data['Frequency'].mean()),
                    float(seg_data['Monetary'].mean())
                ),
                "marketing_actions": SegmentationService._get_marketing_actions(seg_name)
            }
            segment_summary.append(segment_info)
        
        # Cluster centers (denormalized)
        cluster_centers = scaler.inverse_transform(kmeans.cluster_centers_).tolist()
        
        return {
            "segments": rfm.to_dict('records'),
            "segment_summary": segment_summary,
            "cluster_centers": cluster_centers,
            "silhouette_score": sil_score
        }
    
    @staticmethod
    def _assign_segment_name(row: pd.Series, quantiles: pd.DataFrame) -> str:
        """Assign segment name based on RFM values"""
        r_q25, r_q50, r_q75 = quantiles.loc[[0.25, 0.5, 0.75], 'Recency']
        f_q25, f_q50, f_q75 = quantiles.loc[[0.25, 0.5, 0.75], 'Frequency']
        m_q75 = quantiles.loc[0.75, 'Monetary']
        
        if row['Recency'] <= r_q25 and row['Frequency'] >= f_q75 and row['Monetary'] >= m_q75:
            return "Champions"
        elif row['Recency'] <= r_q50 and row['Frequency'] >= f_q50:
            return "Loyal"
        elif row['Recency'] >= r_q75 and row['Frequency'] <= f_q25:
            return "At-Risk"
        elif row['Recency'] >= r_q50 and row['Frequency'] <= f_q50:
            return "Hibernating"
        else:
            return "Regulars"
    
    @staticmethod
    def _get_segment_characteristics(seg_name: str, avg_r: float, avg_f: float, avg_m: float) -> str:
        """Get detailed segment characteristics"""
        templates = {
            "Champions": f"Khách hàng VIP với tần suất mua cao ({avg_f:.1f} đơn), chi tiêu lớn ({avg_m:,.0f}) và mới quay lại ({avg_r:.0f} ngày)",
            "Loyal": f"Khách hàng trung thành với frequency {avg_f:.1f}, monetary {avg_m:,.0f}, recency {avg_r:.0f} ngày",
            "At-Risk": f"Nguy cơ cao! Đã lâu không mua ({avg_r:.0f} ngày), frequency thấp ({avg_f:.1f}), cần hành động ngay",
            "Hibernating": f"Khách hàng ngủ đông, recency {avg_r:.0f} ngày, frequency {avg_f:.1f}, cần remarketing",
            "Regulars": f"Khách hàng thường xuyên ổn định, recency {avg_r:.0f} ngày, frequency {avg_f:.1f}"
        }
        return templates.get(seg_name, f"Phân khúc với R={avg_r:.0f}, F={avg_f:.1f}, M={avg_m:,.0f}")
    
    @staticmethod
    def _get_marketing_actions(seg_name: str) -> List[str]:
        """Get marketing action recommendations"""
        actions = {
            "Champions": [
                "Ưu đãi VIP và early access",
                "Chương trình giới thiệu bạn bè",
                "Gửi sản phẩm mới trước"
            ],
            "Loyal": [
                "Tích điểm và upsell",
                "Ưu đãi sinh nhật",
                "Cross-sell sản phẩm bổ trợ"
            ],
            "At-Risk": [
                "Email 'Chúng tôi nhớ bạn' + mã giảm 15%",
                "Reactivation bundle giá tốt",
                "Khảo sát lý do không mua"
            ],
            "Hibernating": [
                "Chiến dịch remarketing mạnh",
                "Giảm phí vận chuyển",
                "Flash sale độc quyền"
            ],
            "Regulars": [
                "Khuyến mãi định kỳ",
                "Gợi ý sản phẩm tương tự",
                "Tăng giá trị đơn hàng"
            ]
        }
        return actions.get(seg_name, ["Khuyến mãi chung"])


class MarketBasketService:
    """Service for market basket analysis using Apriori algorithm"""
    
    @staticmethod
    def analyze_basket(
        df: pd.DataFrame,
        min_support: float = 0.01,
        min_confidence: float = 0.2,
        max_rules: int = 10
    ) -> Dict[str, Any]:
        """Perform market basket analysis"""
        try:
            # Use top products to keep matrix manageable
            prod_counts = df.groupby('StockCode')['InvoiceNo'].nunique().sort_values(ascending=False)
            keep_prods = set(prod_counts.head(300).index)
            df_filtered = df[df['StockCode'].isin(keep_prods)].copy()
            
            # Create stock to description mapping
            stock_to_desc = df_filtered.groupby('StockCode')['Description'].first().to_dict()
            
            # Create basket matrix
            basket = (
                df_filtered.groupby(['InvoiceNo', 'StockCode'])['Quantity']
                .sum()
                .unstack()
                .fillna(0)
            )
            basket = basket.applymap(lambda x: 1 if x > 0 else 0)
            
            if basket.shape[1] < 2:
                return {
                    "rules": [],
                    "top_rule": None,
                    "insights": ["Không đủ dữ liệu để phân tích"],
                    "metrics": {"total_rules": 0}
                }
            
            # Apriori
            frequent_itemsets = apriori(basket, min_support=min_support, use_colnames=True)
            
            if frequent_itemsets.empty:
                return {
                    "rules": [],
                    "top_rule": None,
                    "insights": ["Không tìm thấy itemsets phổ biến. Thử giảm min_support"],
                    "metrics": {"total_rules": 0}
                }
            
            # Association rules
            rules = association_rules(
                frequent_itemsets, 
                metric="confidence", 
                min_threshold=min_confidence
            )
            
            if rules.empty:
                return {
                    "rules": [],
                    "top_rule": None,
                    "insights": ["Không tìm thấy luật kết hợp. Thử giảm min_confidence"],
                    "metrics": {"total_rules": 0}
                }
            
            # Format rules
            def format_itemset(itemset):
                return [stock_to_desc.get(item, str(item)) for item in sorted(list(itemset))]
            
            rules_formatted = []
            for _, rule in rules.iterrows():
                rules_formatted.append({
                    "antecedents": format_itemset(rule['antecedents']),
                    "consequents": format_itemset(rule['consequents']),
                    "support": float(rule['support']),
                    "confidence": float(rule['confidence']),
                    "lift": float(rule['lift']),
                    "conviction": float(rule.get('conviction', 0)) if pd.notna(rule.get('conviction')) else None
                })
            
            # Sort by confidence and lift
            rules_formatted = sorted(
                rules_formatted, 
                key=lambda x: (x['confidence'], x['lift']), 
                reverse=True
            )[:max_rules]
            
            # Top rule
            top_rule = rules_formatted[0] if rules_formatted else None
            
            # Insights
            insights = MarketBasketService._generate_insights(rules_formatted, top_rule)
            
            return {
                "rules": rules_formatted,
                "top_rule": top_rule,
                "insights": insights,
                "metrics": {
                    "total_rules": len(rules_formatted),
                    "avg_confidence": float(np.mean([r['confidence'] for r in rules_formatted])) if rules_formatted else 0,
                    "avg_lift": float(np.mean([r['lift'] for r in rules_formatted])) if rules_formatted else 0
                }
            }
        
        except Exception as e:
            return {
                "rules": [],
                "top_rule": None,
                "insights": [f"Lỗi phân tích: {str(e)}"],
                "metrics": {"total_rules": 0}
            }
    
    @staticmethod
    def _generate_insights(rules: List[Dict], top_rule: Optional[Dict]) -> List[str]:
        """Generate market basket insights"""
        insights = []
        
        if top_rule:
            conf_pct = top_rule['confidence'] * 100
            insights.append(
                f"Bundle đề xuất hàng đầu: {top_rule['antecedents'][0]} -> {top_rule['consequents'][0]} "
                f"(Confidence: {conf_pct:.1f}%, Lift: {top_rule['lift']:.2f})"
            )
        
        # High confidence rules
        high_conf = [r for r in rules if r['confidence'] > 0.6]
        if high_conf:
            insights.append(f"Tìm thấy {len(high_conf)} luật có confidence >60% - rất phù hợp để tạo bundle")
        
        # High lift rules
        high_lift = [r for r in rules if r['lift'] > 2.0]
        if high_lift:
            insights.append(f"Có {len(high_lift)} cặp sản phẩm với mối liên kết rất mạnh (Lift >2.0)")
        
        return insights


class ChurnService:
    """Service for identifying customers at risk of churning"""
    
    @staticmethod
    def identify_churn_risk(
        df: pd.DataFrame,
        recency_threshold_pct: float = 75,
        frequency_threshold_pct: float = 25
    ) -> Dict[str, Any]:
        """Identify customers at risk of churning"""
        # Calculate RFM
        ref_date = pd.to_datetime(df['InvoiceDate']).max()
        rfm = df.groupby('CustomerID').agg(
            Recency=('InvoiceDate', lambda x: (ref_date - x.max()).days),
            Frequency=('InvoiceNo', pd.Series.nunique),
            Monetary=('Revenue', 'sum')
        ).reset_index()
        
        # Calculate thresholds
        recency_threshold = np.percentile(rfm['Recency'], recency_threshold_pct)
        frequency_threshold = np.percentile(rfm['Frequency'], frequency_threshold_pct)
        
        # Flag at-risk customers
        at_risk = rfm[
            (rfm['Recency'] >= recency_threshold) & 
            (rfm['Frequency'] <= frequency_threshold)
        ].copy()
        
        # Calculate risk score (0-100)
        if len(at_risk) > 0:
            at_risk['ChurnRiskScore'] = (
                (at_risk['Recency'] / rfm['Recency'].max()) * 50 +
                ((rfm['Frequency'].max() - at_risk['Frequency']) / rfm['Frequency'].max()) * 50
            ) * 100
        else:
            at_risk['ChurnRiskScore'] = 0
        
        # Recommended actions
        at_risk['RecommendedActions'] = at_risk.apply(
            lambda row: ChurnService._get_retention_actions(row), 
            axis=1
        )
        
        # Risk summary
        total_customers = len(rfm)
        at_risk_count = len(at_risk)
        risk_pct = (at_risk_count / total_customers * 100) if total_customers > 0 else 0
        potential_value = float(at_risk['Monetary'].sum())
        
        risk_summary = {
            "total_customers": total_customers,
            "at_risk_count": at_risk_count,
            "risk_percentage": risk_pct,
            "avg_risk_score": float(at_risk['ChurnRiskScore'].mean()) if len(at_risk) > 0 else 0,
            "risk_level": "High" if risk_pct > 20 else "Medium" if risk_pct > 10 else "Low"
        }
        
        # Recommendations
        recommendations = ChurnService._generate_recommendations(risk_pct, at_risk_count, potential_value)
        
        return {
            "at_risk_customers": at_risk.to_dict('records'),
            "risk_summary": risk_summary,
            "recommendations": recommendations,
            "potential_value_at_risk": potential_value
        }
    
    @staticmethod
    def _get_retention_actions(row: pd.Series) -> List[str]:
        """Get retention actions for a customer"""
        actions = ["Gửi email cá nhân hóa 'Chúng tôi nhớ bạn'"]
        
        if row['Monetary'] > row.get('Monetary', pd.Series()).quantile(0.75):
            actions.append("Ưu đãi VIP đặc biệt 20% - khách hàng giá trị cao")
        else:
            actions.append("Mã giảm giá 15% có thời hạn")
        
        actions.append("Gợi ý sản phẩm dựa trên lịch sử mua hàng")
        actions.append("Miễn phí vận chuyển cho đơn tiếp theo")
        
        return actions
    
    @staticmethod
    def _generate_recommendations(risk_pct: float, at_risk_count: int, potential_value: float) -> List[str]:
        """Generate retention strategy recommendations"""
        recommendations = []
        
        if risk_pct > 20:
            recommendations.append(f"🚨 CẢNH BÁO CAO: {risk_pct:.1f}% khách hàng có nguy cơ rời bỏ!")
            recommendations.append("Triển khai chiến dịch retention khẩn cấp trong 7 ngày")
        elif risk_pct > 10:
            recommendations.append(f"⚠️ Cảnh báo: {risk_pct:.1f}% khách hàng có nguy cơ rời bỏ")
            recommendations.append("Lên kế hoạch chiến dịch re-engagement trong 14 ngày")
        else:
            recommendations.append(f"✅ Tình hình tốt: Chỉ {risk_pct:.1f}% khách hàng có nguy cơ rời bỏ")
        
        recommendations.append(f"Tiềm năng cứu vãn: {potential_value:,.0f} đơn vị tiền tệ từ {at_risk_count} khách hàng")
        recommendations.append("Ưu tiên khách hàng có Monetary cao nhất")
        recommendations.append("Thiết kế email marketing với urgency (thời hạn giới hạn)")
        recommendations.append("Theo dõi tỷ lệ conversion sau 30 ngày")
        
        return recommendations
