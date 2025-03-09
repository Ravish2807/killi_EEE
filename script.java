// Handle form submission for uploading dataset
document.getElementById("uploadForm").addEventListener("submit", function (e) {
    e.preventDefault();
    const formData = new FormData();
    formData.append("file", document.getElementById("fileInput").files[0]);

    fetch("/upload", {
        method: "POST",
        body: formData,
    })
    .then(response => response.json())
    .then(data => {
        const table = document.createElement('table');
        const df = JSON.parse(data.data);
        const columns = df.columns;
        const rows = df.data;

        // Create table header
        const headerRow = table.insertRow();
        columns.forEach(col => {
            const th = document.createElement('th');
            th.innerText = col;
            headerRow.appendChild(th);
        });

        // Create table rows
        rows.forEach(row => {
            const tableRow = table.insertRow();
            row.forEach(cell => {
                const td = document.createElement('td');
                td.innerText = cell;
                tableRow.appendChild(td);
            });
        });

        document.getElementById("datasetTable").innerHTML = "";
        document.getElementById("datasetTable").appendChild(table);
    });
});

// Handle model selection and display results
document.getElementById("runModelBtn").addEventListener("click", function () {
    const modelType = document.getElementById("modelSelect").value;

    // Get the dataset from the table
    const dataset = [];  // You would need to extract the dataset from the table here

    fetch("/run_model", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ model_type: modelType, dataset: dataset }),
    })
    .then(response => response.json())
    .then(results => {
        document.getElementById("rmse").innerText = results.rmse;
        document.getElementById("mae").innerText = results.mae;

        // Update the chart with model results
        const ctx = document.getElementById('modelChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['RMSE', 'MAE'],
                datasets: [{
                    label: modelType,
                    data: [results.rmse, results.mae],
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    });
});
