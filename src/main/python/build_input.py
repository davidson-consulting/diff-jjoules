import sys
from utils.cmd_utils import *
from run import *

OUTPUT_DIR = 'data/output/december_2020/'
commits_file = '/input'

if __name__ == '__main__':

    repo_url = sys.argv[1]
    project_name = sys.argv[2]

    delete_directory(PATH_V1)
    delete_directory(PATH_V2)

    clone(repo_url, PATH_V1)
    clone(repo_url, PATH_V2)

    create_if_does_not_exist(OUTPUT_DIR + project_name)
    output_file_path = OUTPUT_DIR + project_name + commits_file
    delete_file(output_file_path)

    with open(output_file_path, 'w') as output_file:
        output_file.write(repo_url + '\n')
    git_log(PATH_V1, output_file_path)
