from math import sqrt
import os
import pandas as pd
from scipy.stats import expon, norm, uniform, chi2
import matplotlib.pyplot as plt

def chi_squared_test(observed_frequencies, expected_densities, sample_size, degrees_of_freedom, alpha=0.05):
    chi_squared_value = 0
    for i in range(len(observed_frequencies)):
        if expected_densities[i] != 0:
            chi_squared_value += ((observed_frequencies[i] - expected_densities[i] * sample_size) ** 2) / (expected_densities[i] * sample_size)
        else:
            print('Expected density is 0')
    chi_squared_critical_value = chi2.ppf(1 - alpha, degrees_of_freedom)
    return chi_squared_value, chi_squared_critical_value

def check_distribution(distribution_name, observed_frequencies, expected_densities, sample_size, degrees_of_freedom):
    chi_squared_value, chi_squared_critical_value = chi_squared_test(observed_frequencies, expected_densities, sample_size, degrees_of_freedom)
    print(f'Chi-squared value: {chi_squared_value}\nCritical value: {chi_squared_critical_value}')
    if chi_squared_value < chi_squared_critical_value:
        print(f'Hypothesis of {distribution_name} distribution is accepted')
    else:
        print(f'Hypothesis of {distribution_name} distribution is rejected')

def get_frequency_table(data_values, num_intervals=20):
    min_val = min(data_values)
    max_val = max(data_values)
    interval_length = (max_val - min_val) / num_intervals
    intervals = [min_val + i * interval_length for i in range(num_intervals + 1)]
    frequencies = [0 for _ in range(num_intervals)]
    for value in data_values:
        for i in range(num_intervals):
            if intervals[i] <= value < intervals[i + 1] or (i == num_intervals - 1 and value == intervals[i + 1]):
                frequencies[i] += 1
                break
    return intervals, frequencies

def merge_intervals(intervals, frequencies, min_frequency=5):
    merged_intervals = [intervals[0], intervals[1]]
    merged_frequencies = [frequencies[0]]
    for i in range(2, len(intervals)):
        if merged_frequencies[-1] < min_frequency:
            merged_intervals[-1] = intervals[i]
            merged_frequencies[-1] += frequencies[i - 1]
        else:
            merged_intervals.append(intervals[i])
            merged_frequencies.append(frequencies[i - 1])
    if merged_frequencies[-1] < min_frequency:
        merged_frequencies[-2] += merged_frequencies[-1]
        merged_intervals[-2] = merged_intervals[-1]
        merged_intervals.pop()
        merged_frequencies.pop()
    return merged_intervals, merged_frequencies

def find_mean_and_variance(data_sample):
    sample_mean = sum(data_sample) / len(data_sample)
    sample_variance = sum((x - sample_mean) ** 2 for x in data_sample) / len(data_sample)
    unbiased_sample_variance = sum((x - sample_mean) ** 2 for x in data_sample) / (len(data_sample) - 1)
    return sample_mean, unbiased_sample_variance

def build_histogram(data_sample, intervals, expected_densities, merged_intervals, distribution_name):
    plt.hist(data_sample, bins=intervals, edgecolor='black')
    plt.xticks(intervals, rotation='vertical')
    plt.title(f'Sample frequency histogram ({distribution_name} generator)')
    plt.xlabel('Intervals')
    plt.ylabel('Frequency')
    expected_law = [x * len(data_sample) for x in expected_densities]
    interval_centers = [(merged_intervals[i] + merged_intervals[i + 1]) / 2 for i in range(len(merged_intervals) - 1)]
    plt.plot(interval_centers, expected_law, color='red')
    plt.subplots_adjust(bottom=0.2)
    plt.show()

def task1(data_sample):
    distribution_name = 'exponential'
    sample_mean, sample_variance = find_mean_and_variance(data_sample)
    intervals, frequencies = get_frequency_table(data_sample)
    merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
    expected_densities = []
    for i in range(len(merged_intervals) - 1):
        expected_densities.append(
            expon.cdf(merged_intervals[i + 1], scale=1 / (1/sample_mean)) - expon.cdf(merged_intervals[i], scale=1 / (1/sample_mean))
        )
    build_histogram(data_sample, intervals, expected_densities, merged_intervals, distribution_name)
    degrees_of_freedom = len(merged_frequencies) - 1 - 1
    check_distribution(distribution_name, merged_frequencies, expected_densities, data_sample.size, degrees_of_freedom)
    
    distribution_name = 'normal'
    sample_mean, sample_variance = find_mean_and_variance(data_sample)
    intervals, frequencies = get_frequency_table(data_sample)
    merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
    expected_densities = []
    for i in range(len(merged_intervals) - 1):
        expected_densities.append(
            norm.cdf(merged_intervals[i + 1], loc=sample_mean, scale=sqrt(sample_variance)) -
            norm.cdf(merged_intervals[i], loc=sample_mean, scale=sqrt(sample_variance))
        )
    build_histogram(data_sample, intervals, expected_densities, merged_intervals, distribution_name)
    degrees_of_freedom = len(merged_frequencies) - 1 - 2
    check_distribution(distribution_name, merged_frequencies, expected_densities, data_sample.size, degrees_of_freedom)
    
    distribution_name = 'uniform'
    find_mean_and_variance(data_sample)
    intervals, frequencies = get_frequency_table(data_sample)
    merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
    expected_densities = []
    for i in range(len(intervals) - 1):
        expected_densities.append(
            uniform.cdf(intervals[i + 1], loc=0, scale=1) - uniform.cdf(intervals[i], loc=0, scale=1)
        )
    build_histogram(data_sample, intervals, expected_densities, intervals, distribution_name)
    expected_densities = []
    for i in range(len(merged_intervals) - 1):
        expected_densities.append(
            uniform.cdf(merged_intervals[i + 1], loc=0, scale=1) - uniform.cdf(merged_intervals[i], loc=0, scale=1)
        )
    degrees_of_freedom = len(merged_frequencies) - 1 - 2
    if degrees_of_freedom < 0:
        print('Too few degrees of freedom, test cannot be performed')
    else:
        check_distribution(distribution_name, merged_frequencies, expected_densities, data_sample.size, degrees_of_freedom)

if __name__ == '__main__':
    csv_file_path = os.path.join(os.path.dirname(__file__), 'lawOfDistribution.csv')
    data = pd.read_csv(csv_file_path)
    data_sample = data['TimeInSystem'].values
    task1(data_sample)