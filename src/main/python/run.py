import sys
from utils.cmd_utils import *
from utils.run_args import *
import csv

PREFIX_TMP = '/tmp/'
FOLDER_PATH_V1 = 'v1'
FOLDER_PATH_V2 = 'v2'
PATH_V1 = PREFIX_TMP + FOLDER_PATH_V1
PATH_V2 = PREFIX_TMP + FOLDER_PATH_V2

def get_tests_to_execute():
    path = PATH_V1 + '/' + VALUE_TEST_LISTS
    tests_to_execute = {}
    with open(path, 'r') as csvfile:
        file = csv.reader(csvfile, delimiter=';')
        for line in file:
            tests_to_execute[line[0]] = line[1:]
    return tests_to_execute

def run(nb_iteration, output_path):
    run_mvn_clean_test(PATH_V1)
    run_mvn_clean_test(PATH_V2)
    run_mvn_diff_select(PATH_V1, PATH_V2)
    copy(PATH_V1 + '/' + VALUE_TEST_LISTS, output_path + '/' + VALUE_TEST_LISTS)
    run_mvn_build_classpath_and_instrument(PATH_V1, PATH_V2)
    tests_to_execute = get_tests_to_execute()
    for i in range(nb_iteration):
        print(i)
        run_mvn_test(PATH_V1, tests_to_execute, True)
        copy_jjoules_result(PATH_V1, output_path + '/v1/' + str(i))
        run_mvn_test(PATH_V2, tests_to_execute, True)
        copy_jjoules_result(PATH_V2, output_path + '/v2/' + str(i))

if __name__ == '__main__':

    args = RunArgs().build_parser().parse_args()

    project_name = args.project_name
    commit_sha_v1 = args.sha_v1
    commit_sha_v2 = args.sha_v2
    output_path = args.output
    nb_iteration = int(args.iteration)

    reset_hard(commit_sha_v1, PATH_V1)
    reset_hard(commit_sha_v2, PATH_V2)
    try:
        mkdir(output_path + '/' + project_name + '/' + commit_sha_v1[:6] + '_' + commit_sha_v2[:6])
    except FileExistsError:
        print('pass...')
    run(nb_iteration, output_path + '/' + project_name + '/' + commit_sha_v1[:6] + '_' + commit_sha_v2[:6])
