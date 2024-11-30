from math import sqrt
import os
import pandas as pd
from scipy.stats import expon, norm, uniform, chi2



def chi_squared_test(
        frequencies,
        theoretical_density,
        size_of_sample,
        degrees_of_freedom,
        significance_level=0.05
):
    """
    Perform a chi-squared test for a given sample of values and theoretical density.
    :param frequencies: A list of observed frequencies in different intervals.
    :param theoretical_density: A list of expected theoretical densities corresponding to the intervals.
    :param size_of_sample: The total size of the sample.
    :param degrees_of_freedom: The degrees of freedom for the chi-squared test.
    :param significance_level: The significance level for the chi-squared test. Defaults to 0.05.
    :return: A tuple containing the chi-squared statistic and the critical chi-squared value at the given
             significance level.
    """
    chi_squared = 0
    for i in range(len(frequencies)):
        if(theoretical_density[i]!=0):
            chi_squared += ((frequencies[i] - theoretical_density[i] * size_of_sample) ** 2) / (
                theoretical_density[i] * size_of_sample)
        else:
            print('Theoretical density is 0')
    chi_squared_critical = chi2.ppf(1 - significance_level, degrees_of_freedom)
    return chi_squared, chi_squared_critical


def check_sample_distribution_law(
        distribution_name,
        frequencies,
        theoretic_density,
        size_of_sample,
        degrees_of_freedom
):
    """
    This function is a wrapper that calculates the chi-squared statistic and the critical value for the chi-squared
    test, and then prints the results. It also prints whether the hypothesis about the distribution is accepted or
    rejected based on the test results.
    """
    chi_squared, chi_squared_critical = chi_squared_test(
        frequencies,
        theoretic_density,
        size_of_sample,
        degrees_of_freedom
    )
    print(
        f'Значення χ-квадрат: {chi_squared}'
        f'\nКритичне значення: {chi_squared_critical}'
    )
    if chi_squared < chi_squared_critical:
        print(f'Гіпотеза про {distribution_name} розподіл приймається')
    else:
        print(f'Гіпотеза про {distribution_name} розподіл відхиляється')

import matplotlib.pyplot as plt
def get_frequency_table(values, k=20):
    """
     Generate a frequency table for a given sample of values using a specified number of intervals.
    :param values: A list of numerical values to create a frequency table for.
    :param k: The number of intervals or bins to divide the range of values into. Defaults to 20.
    :return: Two lists, intervals and frequencies, where:
             - intervals: A list of boundaries defining each interval.
             - frequencies: A list of frequencies representing the number of values in each interval.
    """
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


def merge_intervals(intervals, frequencies, min_falls_per_interval=5):
    """
    Merge intervals with frequencies less than min_falls_per_interval. Used for chi-squared test
    :param intervals: A list of intervals, represented as edge values of each interval
    :param frequencies:  A list of corresponding frequencies for each interval.
    :param min_falls_per_interval: The minimum number of falls per interval.
    :return: Two lists, merged_intervals and merged_frequencies, where:
             - merged_intervals: A list of merged intervals after applying the threshold.
             - merged_frequencies: A list of corresponding merged frequencies.
    """
    merged_intervals = [intervals[0], intervals[1]]
    merged_frequencies = [frequencies[0]]
    for i in range(2, len(intervals)):
        if merged_frequencies[-1] < min_falls_per_interval:
            merged_intervals[-1] = intervals[i]
            merged_frequencies[-1] += frequencies[i - 1]
        else:
            merged_intervals.append(intervals[i])
            merged_frequencies.append(frequencies[i - 1])
    if merged_frequencies[-1] < min_falls_per_interval:
        merged_frequencies[-2] += merged_frequencies[-1]
        merged_intervals[-2] = merged_intervals[-1]
        merged_intervals.pop()
        merged_frequencies.pop()
    return merged_intervals, merged_frequencies


def find_sample_mean_and_variance(sample):
    mean = sum(sample) / len(sample)
    variance = sum((x - mean) ** 2 for x in sample) / len(sample)
    unbiased_variance = sum((x - mean) ** 2 for x in sample) / (len(sample) - 1)
    print(
        f'Вибіркове середнє: {mean}'
        f'\nВибіркова дисперсія: {variance}'
        f'\nВиправлена вибіркова дисперсія: {unbiased_variance}'
    )
    return mean, unbiased_variance


def build_histogram(
        sample,
        intervals,
        theoretic_density,
        merged_intervals,
        law_name
):
    # Histogram
    plt.hist(sample, bins=intervals, edgecolor='black')
    plt.xticks(intervals, rotation='vertical')
    plt.title(f'Гістограма частот вибірки ({law_name} генератор)')
    plt.xticks(intervals, rotation='vertical')
    plt.xlabel('Інтервали')
    plt.ylabel('Кількість попадань')

    # Theoretical law
    theoretic_law = [x * len(sample) for x in theoretic_density]
    intervals_centers = [(merged_intervals[i] + merged_intervals[i + 1]) / 2 for i in range(len(merged_intervals) - 1)]
    plt.plot(intervals_centers, theoretic_law, color='red')
    plt.subplots_adjust(bottom=0.2)
    plt.show()



def task1(data):
    law_name = 'експоненційний'
    mean, variance = find_sample_mean_and_variance(data)
    intervals, frequencies = get_frequency_table(data)
    merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
    theoretic_density = []
    for i in range(len(merged_intervals) - 1):
        theoretic_density.append(
            expon.cdf(merged_intervals[i + 1], scale=1 / (1/mean)) - expon.cdf(merged_intervals[i], scale=1 / (1/mean))
        )

    build_histogram(
        data,
        intervals,
        theoretic_density,
        merged_intervals,
        law_name,
      
    )    
    degrees_of_freedom = len(merged_frequencies) - 1 - 1
    check_sample_distribution_law(
            law_name,
            merged_frequencies,
            theoretic_density,
            data.size,
            degrees_of_freedom
        )
    law_name = 'нормальний'
    mean, variance = find_sample_mean_and_variance(data)
    intervals, frequencies = get_frequency_table(data)
    merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
    theoretic_density = []
    for i in range(len(merged_intervals) - 1):
        theoretic_density.append(
            norm.cdf(merged_intervals[i + 1], loc=mean, scale=sqrt(variance)) -
            norm.cdf(merged_intervals[i], loc=mean, scale=sqrt(variance))
        )

    build_histogram(
        data,
        intervals,
        theoretic_density,
        merged_intervals,
        law_name,
    )

        # Task 3
    degrees_of_freedom = len(merged_frequencies) - 1 - 2
    check_sample_distribution_law(
            law_name,
            merged_frequencies,
            theoretic_density,
            data.size,
            degrees_of_freedom
        )
    
    law_name = 'рівномірний'
    find_sample_mean_and_variance(data)
    intervals, frequencies = get_frequency_table(data)
    merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
    theoretic_density = []
    for i in range(len(intervals) - 1):
        theoretic_density.append(
            uniform.cdf(intervals[i + 1], loc=0, scale=1) -
            uniform.cdf(intervals[i], loc=0, scale=1)
        )

    build_histogram(
            data,
            intervals,
            theoretic_density,
            intervals,
            law_name,
           
    )

    theoretic_density = []
    for i in range(len(merged_intervals) - 1):
        theoretic_density.append(
            uniform.cdf(merged_intervals[i + 1], loc=0, scale=1) -
            uniform.cdf(merged_intervals[i], loc=0, scale=1)
        )

        # Task 3
    degrees_of_freedom = len(merged_frequencies) - 1 - 2
    if degrees_of_freedom < 0:
        print('Занадто мала кількість ступенів свободи, перевірка не може бути проведена')
        
    else :
        check_sample_distribution_law(
            law_name,
            merged_frequencies,
            theoretic_density,
            data.size,
            degrees_of_freedom
    )





if __name__ == '__main__':
    csv_file_path_0 = os.path.join(os.path.dirname(__file__), 'lawOfDistribution.csv')
    data = pd.read_csv(csv_file_path_0)
    data = data['TimeInSystem'].values
    task1(data)
