import os

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy.stats import chi2, chisquare, norm

num_bins = 30
alpha = 0.05  # Рівень значущості

def task1(data):
    def compute_expected_values(X, mean_X, n_bins=30):
        max = X.max()
        min = X.min()
        step = (max - min) / n_bins
        expected_vals = []
        observed_vals = []

        current_val = min
        while current_val <= max:
            frequency = np.exp(-(1/mean_X) * current_val) - np.exp(-(1/mean_X) * (current_val + step))
            num_expected = frequency * data.size
            expected_vals.append(num_expected)
            num_observed = ((X >= current_val) & (X < (current_val + step))).sum()
            observed_vals.append(num_observed)
            current_val += step
        
        return observed_vals, expected_vals


        
    mean_val = np.mean(data)
    variance_val = np.var(data)
    max = np.max(data)
    print("Середнє значення: ", mean_val)
    print("Дисперсія: ", variance_val)

        
    # Генеруємо теоретичні та емпіричні значення
    observed_values, expected_values = compute_expected_values(data, mean_val)

    # Об'єднуємо інтервали, якщо кількість влучень в якийсь інтервал менша за 5 
    expected_values_bin, observed_values_bin = combine_small_frequencies(observed_values, expected_values)
        
    # Нормалізуємо очікувані частоти до суми спостережуваних частот
    expected_values_bin = np.array(expected_values_bin) * (np.sum(observed_values_bin) / np.sum(expected_values_bin))
    
    chisquare_result = chisquare(observed_values_bin, expected_values_bin, ddof=1)
    print("\nТест χ^2:\n", chisquare_result)
    print("P-значення: ", chisquare_result.pvalue)
    print("Статистика: ", chisquare_result.statistic)
    df = len(observed_values_bin) - 1 - 1  # Ступені свободи (кількість бінів - 1 - кількість параметрів)
    critical_value = chi2.ppf(1 - alpha, df)
    print("Критичне значення: ", critical_value)
    h0 = critical_value > chisquare_result.statistic
    if(h0):
        print("Нульова гіпотеза про експоненціальний розподіл НЕ відхиляється")
    else:
        print("Нульова гіпотеза про експоненціальний розподіл відхиляється")
        print("\n************************************\n")

    

def task2():
    print("Завдання 2")

    def compute_normal_distribution_values(data, mean, std_dev):
        max_val = data.max()
        min_val = data.min()
        bin_width = (max_val - min_val) / num_bins
        expected_values = []
        observed_values = []

        current_val = min_val
        while current_val <= max_val:
            frequency = np.abs(norm(mean, std_dev).cdf(current_val) - norm(mean, std_dev).cdf(current_val + bin_width))
            expected_count = frequency * sample_size
            expected_values.append(expected_count)
            observed_count = ((data >= current_val) & (data < (current_val + bin_width))).sum()
            observed_values.append(observed_count)
            current_val += bin_width
        
        return observed_values, expected_values
    
    def compute_observed_values(data):
        max_val = data.max()
        min_val = data.min() + 1e-9
        bin_width = (max_val - min_val) / num_bins
        observed_values = []

        current_val = min_val
        while current_val <= max_val:
            observed_count = ((data >= current_val) & (data < (current_val + bin_width))).sum()
            observed_values.append(observed_count)
            current_val += bin_width
        
        return observed_values

    # Parameters
    a_values = [0, 5, 10]
    sigma_values = [1, 2, 5]
    
    fig, axs = plt.subplots(len(a_values), len(sigma_values), figsize=(8, 8))
    
    for i, a in enumerate(a_values):
        for j, sigma in enumerate(sigma_values):
            # Generate random numbers
            random_values = np.random.rand(sample_size, 12)
            normal_values = np.sum(random_values, axis=1) - 6
            data = sigma * normal_values + a
            
            # Plot histogram
            axs[i, j].hist(data, bins=num_bins, density=True, alpha=0.4, color='b', label='Generated Data')
            
            axs[i, j].set_xlabel('Value', fontsize=7)
            axs[i, j].set_ylabel('Frequency', fontsize=7)
            axs[i, j].legend(fontsize=7)
            
            mean_val = np.mean(data)
            print("Середнє значення: ", mean_val)
            print("Дисперсія: ", np.var(data))

            observed_values = compute_observed_values(data)
            observed_values, expected_values = compute_normal_distribution_values(data, mean_val, sigma)
            expected_values_bin, observed_values_bin = combine_small_frequencies(observed_values, expected_values)
            
            # Normalize expected frequencies to the sum of observed frequencies
            expected_values_bin = np.array(expected_values_bin) * (np.sum(observed_values_bin) / np.sum(expected_values_bin))
            df = len(observed_values_bin) - 1 - 2  # Ступені свободи (кількість бінів - 1 - кількість параметрів)
            result = chisquare(observed_values_bin, expected_values_bin, ddof=2)
            critical_value = chi2.ppf(1 - alpha, df)
           

            print("\nChi-Square Test:\n", result)
            print("P-value: ", result.pvalue)
            print("Критичне значення: ", critical_value)
            print("Статистика: ", result.statistic)
            h0 = critical_value > result.statistic
            axs[i, j].set_title(f'A = {a}, Sigma = {sigma}, h0 = {h0}', fontsize=7)
            if h0:
                print("Нульова гіпотеза про нормальний розподіл НЕ відхиляється")
            else:
                print("Нульова гіпотеза про нормальний розподіл відхиляється")
            print("\n************************************\n")

    plt.tight_layout()
    plt.show()

def task3():
    print("Завдання 3")
    def generate_random_numbers(a, c, size):
        z = np.zeros(size)
        x = np.zeros(size)
        z[0] = np.random.randint(0, c)
        for i in range(1, size):
            z[i] = (a * z[i-1]) % c
            x[i] = z[i] / c
        return x

    def chi_square_test(data, bins=10):
        observed_vals, _ = np.histogram(data, bins=bins)
        expected_vals = np.ones(bins) * len(data) / bins
        result = chisquare(observed_vals, expected_vals, ddof=2)
        critical_value = chi2.ppf(0.95, df=bins-1)
        return result, critical_value

    a_values = [5**13, 5**15]
    c_values = [2**31, 2**20]

    fig, axs = plt.subplots(len(a_values), len(c_values), figsize=(10, 8))

    for i, a in enumerate(a_values):
        for j, c in enumerate(c_values):
            random_numbers = generate_random_numbers(a, c, sample_size)
            result, critical_value = chi_square_test(random_numbers)
            
            print("\nChi-Square Test:\n", result)
            print("P-value: ", result.pvalue)
            print("Критичне значення: ", critical_value)
            print("Статистика: ", result.statistic)
            h0 = critical_value > result.statistic
            axs[i, j].hist(random_numbers, bins=10, edgecolor='black')
            axs[i, j].set_title(f'A = {a}, C = {c}, h0 = {h0}', fontsize=7)
            if h0:
                print("Нульова гіпотеза про рівномірий розподіл НЕ відхиляється")
            else:
                print("Нульова гіпотеза про рівномірий розподіл відхиляється")
            print("\n************************************\n")

    plt.tight_layout()
    plt.show()

def combine_small_frequencies(observed_vals, expected_vals):
    obs = np.array(observed_vals.copy())
    exp = np.array(expected_vals.copy())
    for i in range(1, len(obs))[::-1]:
        if (obs[i] <= 5 or exp[i] <= 5) and i < (len(obs)):
            obs[i-1] += obs[i]
            exp[i-1] += exp[i]
            obs[i] = 0
            exp[i] = 0

    for i in range(len(obs)):
        if (obs[i] <= 5 or exp[i] <= 5) and i < (len(obs)-1):
            obs[i+1] += obs[i]
            exp[i+1] += exp[i]
            obs[i] = 0
            exp[i] = 0

    obs = obs[obs != 0]
    exp = exp[exp != 0]
    return exp, obs

if __name__ == '__main__':
    csv_file_path_0 = os.path.join(os.path.dirname(__file__), 'timeInSystemWithRandom.csv')
    data = pd.read_csv(csv_file_path_0)
    data = data['TimeInSystem'].values
    task1(data)
