from utils.messagesUtil import MessageUtil
from service.llmService import LLMService
from utils.loggerUtil import get_logger

logger = get_logger(__name__)

class MessageService:
    def __init__(self):
        self.messageUtil = MessageUtil()
        self.llmService = LLMService()
        logger.info("MessageService initialized")

    def process_message(self, message):
        logger.debug(f"Processing message: {message}")

        if self.messageUtil.isBankSms(message):
            logger.info("Message identified as bank SMS. Invoking LLM service.")
            try:
                result = self.llmService.runLLM(message)
                logger.debug(f"LLM result: {result}")
                return result.dict()
            except Exception as e:
                logger.exception("Failed to process message with LLMService")
                raise
        else:
            logger.warning("Message not identified as bank SMS. Skipping LLM.")
            return {"info": "Message is not a bank SMS and was ignored."}
