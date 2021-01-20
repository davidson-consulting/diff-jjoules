import sys
import os
from args import *
import csv
import json

MVN_CMD = 'mvn'
MVN_OPT_FILE = '-f'
MVN_POM_FILE = 'pom.xml'
MVN_CLEAN_GOAL = 'clean'
MVN_TEST_GOAL = 'test'
MVN_INSTALL_GOAL = 'install'
MVN_SKIP_TEST = '-DskipTests'
MVN_BUILD_CP_GOAL = 'dependency:build-classpath'
MVN_OPT_PATH_CP_OUTPUT = '-Dmdep.outputFile=classpath'

CMD_DIFF_TEST_SELECTION = 'eu.stamp-project:dspot-diff-test-selection:3.1.1-SNAPSHOT:list'
CMD_DIFF_INSTRUMENT = 'fr.davidson:diff-jjoules:instrument'
OPT_PATH_DIR_SECOND_VERSION = '-Dpath-dir-second-version='

OPT_TEST = '-Dtest='
OPT_TEST_LISTS = '-Dtests-list='
VALUE_TEST_LISTS = 'testsThatExecuteTheChange.csv'

def run_command(cmd):
    print(cmd)
    os.system(cmd)

def run_mvn_install(path):
    run_command(' '.join([
        MVN_CMD,
        MVN_OPT_FILE,
        path + '/' + MVN_POM_FILE,
        MVN_CLEAN_GOAL,
        MVN_INSTALL_GOAL,
        MVN_SKIP_TEST,
    ]))

def run_mvn_test(path, tests_to_execute):
    run_command(' '.join([
        MVN_CMD,
        MVN_OPT_FILE,
        path + '/' + MVN_POM_FILE,
        MVN_CLEAN_GOAL,
        MVN_TEST_GOAL,
        OPT_TEST + ','.join([test + '#' + '+'.join(tests_to_execute[test]) for test in tests_to_execute]),
    ]))

def run_mvn_build_cp(path):
    run_command(' '.join([
        MVN_CMD,
        MVN_OPT_FILE,
        path + '/' + MVN_POM_FILE,
        MVN_CLEAN_GOAL,
        MVN_TEST_GOAL,
        MVN_SKIP_TEST,
        MVN_BUILD_CP_GOAL,
        MVN_OPT_PATH_CP_OUTPUT
    ]))

def run_mvn_test_selection(path_first_version, path_second_version):
    run_command(
         ' '.join([
            MVN_CMD,
            MVN_OPT_FILE,
            path_first_version + '/' + MVN_POM_FILE,
            MVN_CLEAN_GOAL,
            CMD_DIFF_TEST_SELECTION,
            OPT_PATH_DIR_SECOND_VERSION + path_second_version,
        ])
    )

def run_mvn_instrument_jjoules(path_first_version, path_second_version):
    run_command(
         ' '.join([
            MVN_CMD,
            MVN_OPT_FILE,
            path_first_version + '/' + MVN_POM_FILE,
            CMD_DIFF_INSTRUMENT,
            OPT_TEST_LISTS + VALUE_TEST_LISTS,
            OPT_PATH_DIR_SECOND_VERSION + path_second_version
        ])
    )

CMD_JJOULES_LOCATE = 'fr.davidson:diff-jjoules:locate'
OPT_DATA_JSON_FIRST_VERSION = '-Dpath-data-json-first-version='
OPT_DATA_JSON_SECOND_VERSION = '-Dpath-data-json-second-version='
OPT_OUTPUT = '-Doutput-path='

def run_mvn_locate_jjoules(path_first_version, path_second_version, tests, data_first_version_path, data_second_version_path, output):
    run_command(
        ' '.join([
            MVN_CMD,
            MVN_OPT_FILE,
            path_first_version + '/' + MVN_POM_FILE,
            OPT_PATH_DIR_SECOND_VERSION  + path_second_version,
            OPT_TEST + tests,
            CMD_JJOULES_LOCATE,
            OPT_DATA_JSON_FIRST_VERSION + data_first_version_path,
            OPT_DATA_JSON_SECOND_VERSION + data_second_version_path,
            OPT_OUTPUT + output
        ])
    )

def get_path_to_selected_tests_csv_file(output_path):
    for dirName, subdirList, fileList in os.walk(output_path):
        for file in fileList:
            if file == VALUE_TEST_LISTS:
                return dirName + '/' + VALUE_TEST_LISTS

def get_tests_to_execute(output_path):
    path = get_path_to_selected_tests_csv_file(output_path)
    tests_to_execute = {}
    with open(path, 'r') as csvfile:
        file = csv.reader(csvfile, delimiter=';')
        for line in file:
            tests_to_execute[line[0]] = line[1:]
    return tests_to_execute

def get_path_to_jjoules_report_folder(root_path_project):
    for dirName, subdirList, fileList in os.walk(root_path_project):
        for subdir in subdirList:
            if subdir == 'jjoules-reports':
                return dirName + '/' + subdir

def read_json(path_to_json):
    with open(path_to_json) as json_file:
        data = json.load(json_file)
    return data

CPU_MJ_KEY = 'package|uJ'
DURATION_NS_KEY = 'duration|ns'
DRAM_MJ_KEY = 'dram|uJ'

def get_energy_data(data):
    print(data)
    return {
        'energy': data[CPU_MJ_KEY],
        'duration': data[DURATION_NS_KEY],
        'dram': data[DRAM_MJ_KEY],
    } if data[CPU_MJ_KEY] > 0 else {}

def get_fullqualified_name_test(json_file):
    return json_file.split('.json')[0]

def avg_on_each_field(entry_a, entry_b):
    avg_entry = {}
    for e in entry_a:
        avg_entry[e] = (entry_a[e] + entry_b[e]) / 2
    return avg_entry

def collect_data(root_path_project, result):
    path_to_jjoules_report = get_path_to_jjoules_report_folder(root_path_project)
    for file in os.listdir(path_to_jjoules_report):
        data = get_energy_data(read_json(path_to_jjoules_report + '/' + file))
        name = get_fullqualified_name_test(file)
        if 'energy' in data:
            energy = data['energy']
            if name in result:
                result[name].append(energy)
            else:
                result[name] = [energy]
    return result

def write_json(path_to_json, data):
    with open(path_to_json, 'w') as outfile:
        outfile.write(json.dumps(data, indent=4))

def run_tests(nb_iteration, first_version_path, second_version_path, tests_to_execute, output):
    result_v1 = {}
    result_v2 = {}
    for i in range(nb_iteration):
        run_mvn_test(first_version_path, tests_to_execute)
        result_v1 = collect_data(first_version_path, result_v1)
        run_mvn_test(second_version_path, tests_to_execute)
        result_v2 = collect_data(second_version_path, result_v2)
    for test in result_v1:
        result_v1[test] = sorted(result_v1[test])[int(len(result_v1[test]) / 2)]
    for test in result_v2:
        result_v2[test] = sorted(result_v2[test])[int(len(result_v2[test]) / 2)]
    write_json(output + '/data_v1.json', result_v1)
    write_json(output + '/data_v2.json', result_v2)
    delta_acc = 0
    for name in result_v1:
        if name in result_v2:
            delta_acc = delta_acc + (result_v2[name] - result_v1[name])
    print(delta_acc)

def select_test_to_locate(output):
    data_v1 = read_json(output + '/data_v1.json')
    data_v2 = read_json(output + '/data_v2.json')

    delta_per_test = {}
    delta_acc = 0
    for test in data_v1:
        if test in data_v2:
            current_delta = data_v2[test] - data_v1[test]
            delta_acc = delta_acc + current_delta
            delta_per_test[test] = current_delta

    selected_test = {}
    for test in delta_per_test:
        if (delta_per_test[test] / delta_acc) * 100 > 25:
            test_name_splitted = test.split('-')
            if not test_name_splitted[0] in selected_test:
                selected_test[test_name_splitted[0]] = []
            selected_test[test_name_splitted[0]].append(test_name_splitted[1])
    
    print(selected_test)
    return selected_test

if __name__ == '__main__':

    args = RunArgs().build_parser().parse_args()

    first_version_path = args.first_version_path
    second_version_path = args.second_version_path
    nb_iteration = args.iteration
    output = args.output

    run_mvn_install(first_version_path)
    run_mvn_install(second_version_path)

    run_mvn_test_selection(first_version_path, second_version_path)

    run_mvn_build_cp(first_version_path)
    run_mvn_build_cp(second_version_path)
    run_mvn_instrument_jjoules(first_version_path, second_version_path)
    run_tests(nb_iteration, first_version_path, second_version_path, get_tests_to_execute(first_version_path), output)

    tests = select_test_to_locate(output)
    formatted_tests = ','.join([test_class_name + '#' + '+'.join(tests[test_class_name]) for test_class_name in tests])
    print(formatted_tests)
    run_mvn_locate_jjoules(
        first_version_path, 
        second_version_path, 
        formatted_tests, 
        output + '/data_v1.json',
        output + '/data_v2.json',
        output
    )