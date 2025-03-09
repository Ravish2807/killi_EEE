from flask import Flask, render_template, request, jsonify
import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.neural_network import MLPRegressor
from sklearn.ensemble import GradientBoostingRegressor
from sklearn.metrics import mean_absolute_error, mean_squared_error
import json
import matplotlib.pyplot as plt
import io
import base64

app = Flask(__name__)

# Route for the homepage
@app.route('/')
def index():
    return render_template('index.html')

# Route to upload CSV file and view dataset
@app.route('/upload', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"})
    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"})
    
    # Read CSV into pandas DataFrame
    df = pd.read_csv(file)
    
    # Convert DataFrame to JSON format to pass to frontend
    data = df.to_json(orient='split')
    return jsonify(data)

# Route to run the selected model and return results
@app.route('/run_model', methods=['POST'])
def run_model():
    model_type = request.json.get('model_type')
    dataset = request.json.get('dataset')
    
    # Convert dataset back from JSON to pandas DataFrame
    df = pd.DataFrame(dataset)
    
    # Prepare features (X) and target (y)
    X = df.drop('target', axis=1)  # Assuming 'target' is the column to predict
    y = df['target']
    
    # Select model
    if model_type == 'RandomForest':
        model = RandomForestRegressor(n_estimators=100)
    elif model_type == 'MLP':
        model = MLPRegressor(hidden_layer_sizes=(50, ), max_iter=1000)
    elif model_type == 'LightGBM':
        model = GradientBoostingRegressor(n_estimators=100)
    
    # Train model
    model.fit(X, y)
    predictions = model.predict(X)
    
    # Calculate performance metrics
    rmse = mean_squared_error(y, predictions, squared=False)
    mae = mean_absolute_error(y, predictions)

    # Return results to frontend
    results = {
        'predictions': predictions.tolist(),
        'rmse': rmse,
        'mae': mae
    }

    return jsonify(results)

# Run the Flask app
if __name__ == "__main__":
    app.run(debug=True)
