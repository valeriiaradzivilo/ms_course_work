import os

import matplotlib.pyplot as plt
import pandas as pd

csv_file_path_0 = os.path.join(os.path.dirname(__file__), 'meanTimeInSystem0.csv')
csv_file_path_1 = os.path.join(os.path.dirname(__file__), 'meanTimeInSystem1.csv')
csv_file_path_2 = os.path.join(os.path.dirname(__file__), 'meanTimeInSystem2.csv')
csv_file_path_3 = os.path.join(os.path.dirname(__file__), 'meanTimeInSystem3.csv')
csv_file_path_4 = os.path.join(os.path.dirname(__file__), 'meanTimeInSystem4.csv')

data_0 = pd.read_csv(csv_file_path_0)
data_1 = pd.read_csv(csv_file_path_1)
data_2 = pd.read_csv(csv_file_path_2)
data_3 = pd.read_csv(csv_file_path_3)
data_4 = pd.read_csv(csv_file_path_4)

data_0 = data_0.sort_values(by='Time')
data_1 = data_1.sort_values(by='Time')
data_2 = data_2.sort_values(by='Time')
data_3 = data_3.sort_values(by='Time')
data_4 = data_4.sort_values(by='Time')

data_0 = data_0.iloc[::100, :]
data_1 = data_1.iloc[::100, :]
data_2 = data_2.iloc[::100, :]
data_3 = data_3.iloc[::100, :]
data_4 = data_4.iloc[::100, :]

plt.figure(figsize=(10, 6))
plt.plot(data_0['Time'], data_0['MeanTimeInSystem'], color='b', label='Run 1')
plt.plot(data_1['Time'], data_1['MeanTimeInSystem'], color='r', label='Run 2')
plt.plot(data_2['Time'], data_2['MeanTimeInSystem'], color='y', label='Run 3')
plt.plot(data_3['Time'], data_3['MeanTimeInSystem'], color='g', label='Run 4')
plt.plot(data_4['Time'], data_4['MeanTimeInSystem'], color='m', label='Run 5')
plt.title('Mean Time in System Over Time')
plt.xlabel('Time')
plt.ylabel('Mean Time in System')
plt.legend()
plt.grid(True)
plt.show()