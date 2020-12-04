import sys
import os
import numpy as np
import matplotlib
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

from utils.graph_args import *
from utils.json_utils import *


def get_max(arrayv1, arrayv2):
    current_max = -1
    for i in range(0, len(arrayv1)):
        if arrayv1[i] > current_max:
            if arrayv2[i] > arrayv1[i]:
                current_max = arrayv2[i]
            else:
                current_max = arrayv1[i]
        elif arrayv2[i] > current_max:
            current_max = arrayv2[i]
    return current_max

def build_graph(energies_v1, durations_v1, energies_v2, durations_v2, labels, output='graph.png'):
    df = pd.DataFrame({'Test': labels,
                    'EnergyV1': energies_v1,
                    'DurationsV1': durations_v1,
                    'EnergyV2': energies_v2,
                    'DurationsV2': durations_v2
                })
    bar_plot = sns.barplot(x='EnergyV1', y='Test', data=df, order=labels, color='red')
    bar_plot = sns.barplot(x='DurationsV1', y='Test', data=df, order=labels)
    bar_plot = sns.barplot(x='EnergyV2', y='Test', data=df, order=labels)
    bar_plot = sns.barplot(x='DurationsV2', y='Test', data=df, order=labels)
    bar_plot.set(xlabel="Energy in uJ", ylabel="Test", title = "Delta SEC V1-V2")
    plt.tight_layout()
    plt.savefig(output)

def get_test_class(key):
    return key.split('-')[0]

def build_data_per_class(data_v1, data_v2):
    energies_v1 = {}
    durations_v1 = {}
    energies_v2 = {}
    durations_v2 = {}
    labels = []
    for key in data_v1:
        test_class_name = get_test_class(key)
        if not test_class_name in labels:
            labels.append(test_class_name)
            energies_v1[test_class_name] = data_v1[key]['energy']
            durations_v1[test_class_name] = data_v1[key]['duration']
            energies_v2[test_class_name] = data_v2[key]['energy']
            durations_v2[test_class_name] = data_v2[key]['duration']
        else:
            energies_v1[test_class_name] = energies_v1[test_class_name] + data_v1[key]['energy']
            durations_v1[test_class_name] = durations_v1[test_class_name] + data_v1[key]['duration']
            energies_v2[test_class_name] = energies_v2[test_class_name] + data_v2[key]['energy']
            durations_v2[test_class_name] = durations_v2[test_class_name] + data_v2[key]['duration']

    delta_energies_v1 = []
    delta_energies_v2 = []
    energies_v1_values = list(energies_v1.values())
    energies_v2_values = list(energies_v2.values())
    delta_durations_v1 = []
    delta_durations_v2 = []
    durations_v1_values = list(durations_v1.values())
    durations_v2_values = list(durations_v2.values())
    for i in range(0, len(energies_v1)):
        delta_energy = energies_v1_values[i] - energies_v2_values[i]
        if delta_energy < 0:
            delta_energies_v1.append(delta_energy)
            delta_energies_v2.append(0)
        else:
            delta_energies_v2.append(delta_energy)
            delta_energies_v1.append(0)
        delta_duration = durations_v1_values[i] - durations_v2_values[i]
        if delta_energy < 0:
            delta_durations_v1.append(delta_duration)
            delta_durations_v2.append(0)
        else:
            delta_durations_v1.append(delta_duration)
            delta_durations_v2.append(0)

    return delta_energies_v1, delta_durations_v1, delta_energies_v2, delta_durations_v2, labels

def get_test_name(key):
    return key.split('-')[1]

def build_data_per_test(data_v1, data_v2, output_path):
    test_per_test_classes = {}
    energies_v1 = {}
    durations_v1 = {}
    energies_v2 = {}
    durations_v2 = {}
    for key in data_v1:
        test_class_name = get_test_class(key)
        if not test_class_name in test_per_test_classes:
            test_per_test_classes[test_class_name] = []
        test_per_test_classes[test_class_name].append(get_test_name(key))
        energies_v1[key] = data_v1[key]['energy']
        durations_v1[key] = data_v1[key]['duration']
        energies_v2[key] = data_v2[key]['energy']
        durations_v2[key] = data_v2[key]['duration']

    for test_class_name in test_per_test_classes:
        current_energies_v1 = []
        current_durations_v1 = []
        current_energies_v2 = []
        current_durations_v2 = []
        labels = []
        for test in test_per_test_classes[test_class_name]:
            labels.append(test)
            current_energies_v1.append(energies_v1[test_class_name + '-' + test])
            current_durations_v1.append(durations_v1[test_class_name + '-' + test])
            current_energies_v2.append(energies_v2[test_class_name + '-' + test])
            current_durations_v2.append(durations_v2[test_class_name + '-' + test])
        build_graph(current_energies_v1, current_durations_v1, current_energies_v2, current_durations_v2, labels, output=output_path + '/' + test_class_name + '-graph_delta.png')

if __name__ == '__main__':

    args = RunArgs().build_parser().parse_args()
    project_name = args.project_name
    path_to_data = args.data_path
    mode = args.mode

    path_to_commit_folders = path_to_data + '/' + project_name + '/'

    for file in os.listdir(path_to_commit_folders):
        path_to_file = path_to_commit_folders + file
        if file == 'input':
            continue
        print('generate', mode, 'for', file)
        data_v1 = read_json(path_to_file  + '/avg_v1.json')
        data_v2 = read_json(path_to_file  + '/avg_v2.json')

        if mode == mode.per_class:
            energies_v1, durations_v1, energies_v2, durations_v2, labels = build_data_per_class(data_v1, data_v2)
            build_graph(energies_v1, durations_v1, energies_v2, durations_v2, labels, output=path_to_file + '/'+ project_name +'_delta.png')
        elif mode == mode.per_test:
            build_data_per_test(data_v1, data_v2, path_to_file)
        else:
            print('unkown mode', mode)
