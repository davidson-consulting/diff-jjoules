import sys
from utils.cmd_utils import *
import csv

PREFIX_TMP = '/tmp/'
FOLDER_PATH_V1 = 'v1'
FOLDER_PATH_V2 = 'v2'
PATH_V1 = PREFIX_TMP + FOLDER_PATH_V1
PATH_V2 = PREFIX_TMP + FOLDER_PATH_V2

NB_IT = 2

PREFIX_OUTPUT = 'data/output/november_2020/'

def get_tests_to_execute():
    path = PATH_V1 + '/' + VALUE_TEST_LISTS
    tests_to_execute = {}
    with open(path, 'r') as csvfile:
        file = csv.reader(csvfile, delimiter=';')
        for line in file:
            tests_to_execute[line[0]] = line[1:]
    return tests_to_execute

def run(output_path):
    run_mvn_clean_test(PATH_V1)
    run_mvn_clean_test(PATH_V2)
    run_mvn_diff_select(PATH_V1, PATH_V2)
    run_mvn_build_classpath_and_instrument(PATH_V1, PATH_V2)
    tests_to_execute = get_tests_to_execute()
    for i in range(NB_IT):
        print(i)
        run_mvn_test(PATH_V1, tests_to_execute, True)
        copy_jjoules_result(PATH_V1, output_path + '_v1/' + str(i))
        run_mvn_test(PATH_V2, tests_to_execute, True)
        copy_jjoules_result(PATH_V2, output_path + '_v2/' + str(i))

if __name__ == '__main__':

    project_name = sys.argv[1]
    commit_sha_v1 = sys.argv[2]
    commit_sha_v2 = sys.argv[3]

    reset_hard(commit_sha_v1, PATH_V1)
    reset_hard(commit_sha_v2, PATH_V2)

    run(PREFIX_OUTPUT + project_name + '/' + commit_sha_v1[:6] + '_' + commit_sha_v2[:6])
