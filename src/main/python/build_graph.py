import sys
import os
import numpy as np
import matplotlib
import matplotlib.pyplot as plt
import pandas as pd

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
    fig = plt.figure()

    tkw = dict(size=4, width=1)

    energy_v1_ax = fig.add_subplot(111)
    duration_v1_ax = energy_v1_ax.twiny()
    energy_v2_ax = energy_v1_ax.twiny()
    duration_v2_ax = energy_v1_ax.twiny()

    energy_v1_ax.xaxis.label.set_color('blue')
    duration_v1_ax.xaxis.label.set_color('cyan')
    energy_v2_ax.xaxis.label.set_color('orange')
    duration_v2_ax.xaxis.label.set_color('gold')

    energy_v1_ax.tick_params(axis='x', colors='blue', **tkw)
    duration_v1_ax.tick_params(axis='x', colors='cyan', **tkw)
    energy_v2_ax.tick_params(axis='x', colors='orange', **tkw)
    duration_v2_ax.tick_params(axis='x', colors='gold', **tkw)

    energy_v1_ax.set_xlabel('Energy V1(uJ)')
    duration_v1_ax.set_xlabel('Duration V1(s)')
    energy_v2_ax.set_xlabel('Energy V2(uJ)')
    duration_v2_ax.set_xlabel('Duration V2(s)')

    energy_v1_ax.tick_params(axis='both', which='major', labelsize=14)
    energy_v1_ax.tick_params(axis='both', which='minor', labelsize=14)
    duration_v1_ax.tick_params(axis='both', which='major', labelsize=14)
    duration_v1_ax.tick_params(axis='both', which='minor', labelsize=14)
    energy_v2_ax.tick_params(axis='both', which='major', labelsize=14)
    energy_v2_ax.tick_params(axis='both', which='minor', labelsize=14)
    duration_v2_ax.tick_params(axis='both', which='major', labelsize=14)
    duration_v2_ax.tick_params(axis='both', which='minor', labelsize=14)

    max_energy = get_max(energies_v1, energies_v2)
    max_duration = get_max(durations_v1, durations_v2)

    energy_v1_ax.set_xlim([0, max_energy])
    duration_v1_ax.set_xlim([0, max_duration])
    energy_v2_ax.set_xlim([0, max_energy])
    duration_v2_ax.set_xlim([0, max_duration])

    width = 0.1
    df = pd.DataFrame({
        'EnergyV1': energies_v1,
        'DurationV1': durations_v1,
        'EnergyV2': energies_v2,
        'DurationV2': durations_v2
    }, index=labels)
    df.EnergyV1.plot.barh(color='blue', ax=energy_v1_ax, width=width, position=3)
    df.DurationV1.plot.barh(color='cyan', ax=duration_v1_ax, width=width, position=2)
    df.EnergyV2.plot.barh(color='orange', ax=energy_v2_ax, width=width, position=1)
    df.DurationV2.plot.barh(color='gold', ax=duration_v2_ax, width=width, position=0)

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
    return list(energies_v1.values()), list(durations_v1.values()), list(energies_v2.values()), list(durations_v2.values()), labels

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
        build_graph(current_energies_v1, current_durations_v1, current_energies_v2, current_durations_v2, labels, output=output_path + '/' + test_class_name + '-graph.png')

if __name__ == '__main__':

    args = RunArgs().build_parser().parse_args()
    project_name = args.project_name
    path_to_data = args.data_path
    mode = args.mode

    path_to_commit_folders = path_to_data + '/' + project_name + '/'

    for file in os.listdir(path_to_commit_folders)[0:1]:
        path_to_file = path_to_commit_folders + file
        data_v1 = read_json(path_to_file  + '/avg_v1.json')
        data_v2 = read_json(path_to_file  + '/avg_v2.json')

        if mode == mode.per_class:
            energies_v1, durations_v1, energies_v2, durations_v2, labels = build_data_per_class(data_v1, data_v2)
            build_graph(energies_v1, durations_v1, energies_v2, durations_v2, labels, output=path_to_file + '/graph.png')
        elif mode == mode.per_test:
            build_data_per_test(data_v1, data_v2, path_to_file)
        else:
            print('unkown mode', mode)
