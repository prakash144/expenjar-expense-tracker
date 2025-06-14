import os
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.utils.function_calling import convert_to_openai_tool
from langchain_openai import ChatOpenAI
from langchain_mistralai import ChatMistralAI
from langchain_groq import ChatGroq
from dotenv import load_dotenv
from config import LLM_EXTRACTION_PROMPT
from utils.loggerUtil import get_logger
from service.Expense import Expense

logger = get_logger(__name__)

class LLMService:
    def __init__(self):
        load_dotenv()
        self.model_provider = os.getenv("LLM_PROVIDER", "mistral").lower()
        self.model_name = os.getenv("LLM_MODEL", "mistral-large-latest")
        self.api_key = os.getenv("LLM_API_KEY")

        if not self.api_key:
            logger.error("Missing LLM_API_KEY in environment")
            raise ValueError("Missing LLM_API_KEY")

        self.prompt = ChatPromptTemplate.from_messages([
            ("system", LLM_EXTRACTION_PROMPT),
            ("human", "{text}")
        ])

        self.llm = self._load_model()
        self.runnable = self.prompt | self.llm.with_structured_output(schema=Expense)

        logger.info(f"LLMService initialized using provider='{self.model_provider}', model='{self.model_name}'")

    def _load_model(self):
        if self.model_provider == "openai":
            return ChatOpenAI(api_key=self.api_key, model=self.model_name, temperature=0)
        elif self.model_provider == "mistral":
            return ChatMistralAI(api_key=self.api_key, model=self.model_name, temperature=0)
        elif self.model_provider == "groq":
            return ChatGroq(api_key=self.api_key, model=self.model_name, temperature=0)
        else:
            logger.error(f"Unsupported LLM provider: {self.model_provider}")
            raise ValueError(f"Unsupported LLM provider: {self.model_provider}")

    def runLLM(self, message: str):
        logger.debug(f"Invoking LLM with message: {message}")
        try:
            return self.runnable.invoke({"text": message})
        except Exception as e:
            logger.exception("LLM invocation failed")
            raise
