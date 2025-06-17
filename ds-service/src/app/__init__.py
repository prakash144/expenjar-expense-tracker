import sys, os, json
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from flask import Flask, request, jsonify
from service.messageService import MessageService
from utils.loggerUtil import get_logger
from kafka import KafkaProducer
from config import KAFKA_TOPIC

# Initialize Flask and logger
app = Flask(__name__)
logger = get_logger(__name__)

# Load configuration
app.config.from_pyfile('config.py')

# Initialize service
messageService = MessageService()
producer = KafkaProducer(bootstrap_servers=['localhost:9092'],
                        value_serializer=lambda v: json.dumps(v).encode('utf-8'))


@app.route('/v1/ds/message', methods=['POST'])
def handle_message():
    logger.info("Received POST request at /v1/ds/message")
    data = request.get_json()

    if not data or 'message' not in data:
        logger.warning("Bad request: Missing 'message' in request body")
        return jsonify({'error': 'Missing "message" in request body'}), 400

    message = data['message']
    try:
        logger.debug(f"Processing message: {message}")
        result = messageService.process_message(message)
        serialized_result = result.dict()

        # Send the serialized result to the Kafka topic
        producer.send(KAFKA_TOPIC, serialized_result)
        
        logger.info("Message processed successfully")
        return jsonify(serialized_result)
    except Exception as e:
        logger.exception("Error while processing message")
        return jsonify({'error': str(e)}), 500


@app.route("/", methods=['GET'])
def handle_get():
    logger.info("Health check: GET /")
    return "Data-Science Service"


@app.route("/health", methods=['GET'])
def health_check():
    logger.info("Health check: GET /health")
    return jsonify({"status": "UP"})


if __name__ == "__main__":
    logger.info("Starting Flask app on http://localhost:8000")
    app.run(host="localhost", port=8000, debug=True)  # Set debug=False in production
