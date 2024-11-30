from math import sqrt
import os
import pandas as pd
from scipy.stats import expon, norm, uniform, chi2
import matplotlib.pyplot as plt

def chi_squared_test(obs_freq, exp_density, sample_size, df, alpha=0.05):
    chi_squared = 0
    for i in range(len(obs_freq)):
        if exp_density[i] != 0:
            chi_squared += ((obs_freq[i] - exp_density[i] * sample_size) ** 2) / (exp_density[i] * sample_size)
        else:
            print('Expected density is 0')
    chi_squared_critical = chi2.ppf(1 - alpha, df)
    return chi_squared, chi_squared_critical

def check_distribution(name, obs_freq, exp_density, sample_size, df):
    chi_squared, chi_squared_critical = chi_squared_test(obs_freq, exp_density, sample_size, df)
    print(f'Chi-squared value: {chi_squared}\nCritical value: {chi_squared_critical}')
    if chi_squared < chi_squared_critical:
        print(f'Hypothesis of {name} distribution is accepted')
    else:
        print(f'Hypothesis of {name} distribution is rejected')

def get_frequency_table(values, k=20):
    min_value = min(values)
    max_value = max(values)
    interval_length = (max_value - min_value) / k
    intervals = [min_value + i * interval_length for i in range(k + 1)]
    frequencies = [0 for _ in range(k)]
    for value in values:
        for i in range(k):
            if intervals[i] <= value < intervals[i + 1] or (i == k - 1 and value == intervals[i + 1]):
                frequencies[i] += 1
                break
    return intervals, frequencies

def merge_intervals(intervals, frequencies, min_freq=5):
    merged_intervals = [intervals[0], intervals[1]]
    merged_frequencies = [frequencies[0]]
    for i in range(2, len(intervals)):
        if merged_frequencies[-1] < min_freq:
            merged_intervals[-1] = intervals[i]
            merged_frequencies[-1] += frequencies[i - 1]
        else:
            merged_intervals.append(intervals[i])
            merged_frequencies.append(frequencies[i - 1])
    if merged_frequencies[-1] < min_freq:
        merged_frequencies[-2] += merged_frequencies[-1]
        merged_intervals[-2] = merged_intervals[-1]
        merged_intervals.pop()
        merged_frequencies.pop()
    return merged_intervals, merged_frequencies

def find_mean_and_variance(sample):
    mean = sum(sample) / len(sample)
    variance = sum((x - mean) ** 2 for x in sample) / len(sample)
    unbiased_variance = sum((x - mean) ** 2 for x in sample) / (len(sample) - 1)
    print(f'Mean: {mean}\nVariance: {variance}\nUnbiased sample variance: {unbiased_variance}')
    return mean, unbiased_variance

def build_histogram(sample, intervals, exp_density, merged_intervals, name):
    plt.hist(sample, bins=intervals, edgecolor='black')
    plt.xticks(intervals, rotation='vertical')
    plt.title(f'Frequency histogram ({name})')
    plt.xlabel('Intervals')
    plt.ylabel('Frequency')
    exp_law = [x * len(sample) for x in exp_density]
    interval_centers = [(merged_intervals[i] + merged_intervals[i + 1]) / 2 for i in range(len(merged_intervals) - 1)]
    plt.plot(interval_centers, exp_law, color='red')
    plt.subplots_adjust(bottom=0.2)
    plt.show()

def task1(data):
    name = 'exponential'
    mean, variance = find_mean_and_variance(data)
    intervals, frequencies = get_frequency_table(data)
    merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
    exp_density = []
    for i in range(len(merged_intervals) - 1):
        exp_density.append(
            expon.cdf(merged_intervals[i + 1], scale=1 / (1/mean)) - expon.cdf(merged_intervals[i], scale=1 / (1/mean))
        )
    build_histogram(data, intervals, exp_density, merged_intervals, name)
    df = len(merged_frequencies) - 1 - 1
    check_distribution(name, merged_frequencies, exp_density, data.size, df)
    
    name = 'normal'
    mean, variance = find_mean_and_variance(data)
    intervals, frequencies = get_frequency_table(data)
    merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
    exp_density = []
    for i in range(len(merged_intervals) - 1):
        exp_density.append(
            norm.cdf(merged_intervals[i + 1], loc=mean, scale=sqrt(variance)) -
            norm.cdf(merged_intervals[i], loc=mean, scale=sqrt(variance))
        )
    build_histogram(data, intervals, exp_density, merged_intervals, name)
    df = len(merged_frequencies) - 1 - 2
    check_distribution(name, merged_frequencies, exp_density, data.size, df)
    
    name = 'uniform'
    find_mean_and_variance(data)
    intervals, frequencies = get_frequency_table(data)
    merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
    exp_density = []
    for i in range(len(intervals) - 1):
        exp_density.append(
            uniform.cdf(intervals[i + 1], loc=0, scale=1) - uniform.cdf(intervals[i], loc=0, scale=1)
        )
    build_histogram(data, intervals, exp_density, intervals, name)
    exp_density = []
    for i in range(len(merged_intervals) - 1):
        exp_density.append(
            uniform.cdf(merged_intervals[i + 1], loc=0, scale=1) - uniform.cdf(merged_intervals[i], loc=0, scale=1)
        )
    df = len(merged_frequencies) - 1 - 2
    if df < 0:
        print('Too few degrees of freedom, test cannot be performed')
    else:
        check_distribution(name, merged_frequencies, exp_density, data.size, df)

if __name__ == '__main__':
    csv_file_path = os.path.join(os.path.dirname(__file__), 'lawOfDistribution.csv')
    data = pd.read_csv(csv_file_path)
    data = data['TimeInSystem'].values
    task1(data)