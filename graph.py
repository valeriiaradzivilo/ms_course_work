import os

import matplotlib.pyplot as plt
import pandas as pd

# Define the path to the CSV file
csv_file_path = os.path.join(os.path.dirname(__file__), 'data.csv')

# Read the CSV file
data = pd.read_csv(csv_file_path)

# Plot the data
plt.figure(figsize=(10, 6))
plt.plot(data['Size'], data['MeanTimeInSystem'], marker='o', linestyle='-', color='b')
plt.title('Mean Time in System')
plt.xlabel('Size')
plt.ylabel('Mean Time in System')
plt.grid(True)
plt.show()