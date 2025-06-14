## 📦 `ds-service` - Microservice for Extracting Expenses from SMS

### 🧾 Overview
- `ds-service` is a Python microservice.
- It extracts structured data (like amount, merchant, currency) from SMS messages.
- The extracted data is published to Kafka for other services to consume.
- Example input SMS:
  > "INR 294.99 spent on ICICI Bank Card XX7003 on 23-Apr-24 at TW Coffee Kora..."
- Extracted output (Kafka message schema):
  ```json
  {
    "amount": 294.99,
    "merchant": "TW Coffee Kora",
    "currency": "INR"
  }
  ```

---

### ⚙️ Getting Started

#### ✅ Prerequisites
- Install Python 3 and verify with:
  ```bash
  python3 --version
  ```

#### 🧪 Setup Virtual Environment
- Create and activate a virtual environment:
  ```bash
  python -m venv dsenv
  source dsenv/bin/activate
  ```
- Open project in VS Code or IDE:
  ```bash
  code .
  ```

---

### 📁 Project Structure

```
project-root/
│
├── dsenv/                  # Virtual environment directory
└── src/
    └── app/
        ├── __init__.py
        ├── config.py
        │
        ├── service/
        │   ├── Expense.py
        │   ├── llmService.py
        │   └── messageService.py
        │
        └── utils/
            ├── messageUtils.py
            └── loggerUtil.py
```

---
