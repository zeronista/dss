# config.py
# Configuration settings for the API
# Author: GitHub Copilot

from pydantic_settings import BaseSettings
from typing import List

class Settings(BaseSettings):
    """API Configuration"""
    
    # API Settings
    API_TITLE: str = "Online Retail DSS API"
    API_VERSION: str = "1.0.0"
    API_HOST: str = "0.0.0.0"
    API_PORT: int = 8000
    API_WORKERS: int = 4
    
    # CORS Settings
    CORS_ORIGINS: List[str] = ["*"]
    CORS_ALLOW_CREDENTIALS: bool = True
    CORS_ALLOW_METHODS: List[str] = ["*"]
    CORS_ALLOW_HEADERS: List[str] = ["*"]
    
    # Data Settings
    MAX_FILE_SIZE: int = 50 * 1024 * 1024  # 50MB
    SESSION_TIMEOUT: int = 3600  # 1 hour
    DEFAULT_CSV_PATH: str = "online_retail.csv"
    
    # Model Settings
    FORECAST_MAX_PERIODS: int = 12
    FORECAST_DEFAULT_PERIODS: int = 3
    FORECAST_DEFAULT_CONFIDENCE: float = 0.90
    
    SEGMENTATION_MIN_CLUSTERS: int = 2
    SEGMENTATION_MAX_CLUSTERS: int = 10
    SEGMENTATION_DEFAULT_CLUSTERS: int = 4
    
    MARKET_BASKET_MIN_SUPPORT: float = 0.001
    MARKET_BASKET_MAX_SUPPORT: float = 0.5
    MARKET_BASKET_DEFAULT_SUPPORT: float = 0.01
    MARKET_BASKET_DEFAULT_CONFIDENCE: float = 0.2
    MARKET_BASKET_MAX_RULES: int = 100
    MARKET_BASKET_TOP_PRODUCTS: int = 300
    
    # Database (Optional)
    DATABASE_URL: str = None
    MONGODB_URL: str = None
    
    # Redis Cache (Optional)
    REDIS_URL: str = None
    CACHE_TTL: int = 300  # 5 minutes
    
    # Logging
    LOG_LEVEL: str = "INFO"
    
    class Config:
        env_file = ".env"
        case_sensitive = True

settings = Settings()
