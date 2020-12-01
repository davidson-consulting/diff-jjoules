from utils.cmd_utils import *
from run import *
import sys

if __name__ == '__main__':

    repo_url = sys.argv[1]
    clone(repo_url, PATH_V1)
    clone(repo_url, PATH_V2)
