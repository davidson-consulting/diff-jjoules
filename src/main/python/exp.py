import sys
from utils.cmd_utils import *

folder_name_first_version = 'v1'
folder_name_second_version = 'v2'

if __name__ == '__main__':

    repo_url = sys.argv[1]
    commit_sha_v1 = sys.argv[2]
    commit_sha_v2 = sys.argv[3]

    clone(repo_url, folder_name_first_version)
    clone(repo_url, folder_name_first_version)

    reset_hard(commit_sha_v1, folder_name_first_version)
    reset_hard(commit_sha_v2, folder_name_second_version)
