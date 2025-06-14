import re

class MessageUtil:
    def isBankSms(self, message):
        # Common keywords found in bank/card transaction SMS
        keywords = [
            "spent", "debited", "credited", "purchase", "transaction", "card", "INR", "Rs.", "$", 
            "available balance", "txn", "account", "payment", "paid", "withdrawn", "amount", "POS", 
            "merchant", "successful", "fail", "ATM", "wallet", "credited to", "refunded"
        ]
        pattern = r'\b(?:' + '|'.join(re.escape(word) for word in keywords) + r')\b'
        return bool(re.search(pattern, message, flags=re.IGNORECASE))