import os

import matplotlib.pyplot as plt
import pandas as pd

csv_file_path_0 = os.path.join(os.path.dirname(__file__), 'meanTimeInSystem0.csv')
csv_file_path_1 = os.path.join(os.path.dirname(__file__), 'meanTimeInSystem1.csv')
csv_file_path_2 = os.path.join(os.path.dirname(__file__), 'meanTimeInSystem2.csv')

data_0 = pd.read_csv(csv_file_path_0)
data_1 = pd.read_csv(csv_file_path_1)
data_2 = pd.read_csv(csv_file_path_2)

data_0 = data_0.sort_values(by='Time')
data_1 = data_1.sort_values(by='Time')
data_2 = data_2.sort_values(by='Time')

plt.figure(figsize=(10, 6))
plt.plot(data_0['Time'], data_0['MeanTimeInSystem'], color='b', label='meanTimeInSystem1')
plt.plot(data_1['Time'], data_1['MeanTimeInSystem'], color='r', label='meanTimeInSystem1')
plt.plot(data_2['Time'], data_2['MeanTimeInSystem'], color='y', label='meanTimeInSystem2')
plt.title('Mean Time in System Over Time')
plt.xlabel('Time')
plt.ylabel('Mean Time in System')
plt.legend()
plt.grid(True)
plt.show()