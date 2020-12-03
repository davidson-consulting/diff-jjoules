import sys
import os

from utils.data_args import *
from utils.json_utils import *

def get_class_name(json_file):
    return json_file.split('.json')[0]

def compute_energy_for_tests(path_to_folder_iteration):
    compute_energy_for_tests = {}
    for json_file in os.listdir(path_to_folder_iteration):
        key = get_class_name(json_file)
        data = get_energy_data(read_json(path_to_folder_iteration + '/' + json_file))
        compute_energy_for_tests[key] = data
    return compute_energy_for_tests

def compute_avg_energy_for_iterations(path_to_data_version):
    iteration_folders = os.listdir(path_to_data_version)
    avg_energy_per_test = compute_energy_for_tests(path_to_data_version + '/' + iteration_folders[0])
    for iteration_folder in iteration_folders[1:]:
        current_avg_energy_per_test = compute_energy_for_tests(path_to_data_version + '/' + iteration_folder)
        for test in avg_energy_per_test:
            avg_energy_per_test[test] = avg_on_each_field(avg_energy_per_test[test], current_avg_energy_per_test[test])
    return avg_energy_per_test

def compute_avg_energy_for_commit(path_to_data_commit):
    path_v1 = path_to_data_commit + '/v1/'
    path_v2 = path_to_data_commit + '/v2/'
    data_v1 = compute_avg_energy_for_iterations(path_v1)
    data_v2 = compute_avg_energy_for_iterations(path_v2)
    return data_v1, data_v2

if __name__ == '__main__':

    args = RunArgs().build_parser().parse_args()
    project_name = args.project_name
    path_to_data = args.data_path

    path_to_commit_folders = path_to_data + '/' + project_name + '/'

    for file in os.listdir(path_to_commit_folders):
        path_to_file = path_to_commit_folders + file
        print(file)
        if os.path.isdir(path_to_file):
            data_v1, data_v2 = compute_avg_energy_for_commit(path_to_file)
            write_json(path_to_file + '/avg_v1.json', data_v1)
            write_json(path_to_file + '/avg_v2.json', data_v2)
