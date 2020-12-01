import os
from shutil import copyfile, SameFileError, copytree

POM_FILE = '/pom.xml'
JJOULED_POM_FILE = '/pom_jjouled.xml'
MVN_CMD = 'mvn -Drat.skip=true -Djacoco.skip=true -Danimal.sniffer.skip=true -f '
MVN_CLEAN_GOAL = 'clean'
MVN_TEST = 'test'
OPT_TEST = '-Dtest='

def run_command(command):
    print(command)
    os.system(command)

def run_mvn_test(path, tests_to_execute, jjouled=False):
    run_command(
        ' '.join([
            MVN_CMD,
            path + (JJOULED_POM_FILE if jjouled else POM_FILE),
            MVN_CLEAN_GOAL,
            MVN_TEST,
            OPT_TEST + ','.join([test + '#' + '+'.join(tests_to_execute[test]) for test in tests_to_execute])
        ])
    )

CMD_DIFF_TEST_SELECTION = 'eu.stamp-project:dspot-diff-test-selection:3.1.1-SNAPSHOT:list'
CMD_DIFF_INSTRUMENT = 'fr.davidson:diff-jjoules:instrument'
OPT_PATH_DIR_SECOND_VERSION = '-Dpath-dir-second-version='
OPT_TEST_LISTS = '-Dtests-list='
VALUE_TEST_LISTS = 'testsThatExecuteTheChange.csv'

def run_mvn_diff_select(path_first_version, path_second_version):
    run_command(
         ' '.join([
            MVN_CMD,
            path_first_version + POM_FILE,
            MVN_CLEAN_GOAL,
            CMD_DIFF_TEST_SELECTION,
            OPT_PATH_DIR_SECOND_VERSION + path_second_version
        ])
    )

def run_mvn_instrument(path_first_version, path_second_version):
    run_command(
         ' '.join([
            MVN_CMD,
            path_first_version + POM_FILE,
            CMD_DIFF_INSTRUMENT,
            OPT_TEST_LISTS + path_first_version + '/' + VALUE_TEST_LISTS,
            OPT_PATH_DIR_SECOND_VERSION + path_second_version
        ])
    )

MVN_SKIP_TEST = '-DskipTests'

def run_mvn_clean_test(path):
    run_command(
        ' '.join([
            MVN_CMD,
            path + POM_FILE,
            MVN_CLEAN_GOAL,
            MVN_TEST,
            MVN_SKIP_TEST,
        ])
    )

BUILD_CLASSPATH_GOAL = 'dependency:build-classpath'
OPT_OUTPUT_CP_FILE = '-Dmdep.outputFile=classpath'


def run_mvn_build_classpath_and_instrument(path_first_version, path_second_version):
    run_command(
         ' '.join([
            MVN_CMD,
            path_first_version + POM_FILE,
            BUILD_CLASSPATH_GOAL,
            OPT_OUTPUT_CP_FILE,
            CMD_DIFF_INSTRUMENT,
            OPT_TEST_LISTS + path_first_version + '/' + VALUE_TEST_LISTS,
            OPT_PATH_DIR_SECOND_VERSION + path_second_version
        ])
    )

JJOULES_REPORT_FOLDER = 'target/jjoules-reports'

def copy_jjoules_result(src_dir, dst):
    src = src_dir + '/' + JJOULES_REPORT_FOLDER
    print('copy dir', src,  dst)
    copy_directory(src, dst)

def copy_directory(src, dst):
    try:
        copytree(src, dst)
    except (SameFileError):
       print('src and dst same file... passing', src)

CMD_GIT_CLONE = 'git clone'

def clone(url, folder_path):
    run_command(
        ' '.join([
            CMD_GIT_CLONE,
            url,
            folder_path
        ])
    )

CMD_GIT_RESET_HARD = 'git reset --hard'

def reset_hard(commitsha, git_repo_path):
    cwd = os.getcwd()
    os.chdir(git_repo_path)
    run_command(
        ' '.join([
            CMD_GIT_RESET_HARD,
            commitsha,
        ])
    )
    os.chdir(cwd)
