LLM_EXTRACTION_PROMPT = (
    "You are an expert extraction algorithm. Extract only relevant structured information from the text: "
    "amount (numeric only, without currency symbol), currency (like INR, USD), and merchant name. "
    "If any value is missing or unclear, return null for that field. Do not guess."
)

KAFKA_TOPIC="expense_service"