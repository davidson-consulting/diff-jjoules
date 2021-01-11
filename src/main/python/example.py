import sys
import os

def run_command(cmd):
    print(cmd)
    os.system(cmd)

CMD_GIT = 'git'
CMD_GIT_CLONE = CMD_GIT + ' clone'

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

def delete_file(file_path):
    try:
        os.remove(file_path)
    except (FileNotFoundError):
        print(file_path, 'does not exist. Pass...')

def delete_module_info_java(path):
    for dirName, subdirList, fileList in os.walk(path):
        for file in fileList:
            if file == 'module-info.java':
                delete_file(dirName + '/' + file)

if __name__ == '__main__':

    clone('http://github.com/google/gson', '/tmp/example_v1')
    clone('http://github.com/google/gson', '/tmp/example_v2')
    reset_hard('f0aa1118e9ef66ed324f9a63cdfb551cb4e9eca5', '/tmp/example_v1')
    reset_hard('7a9fd5962dce7f277efa15fcc996606be0733bac', '/tmp/example_v2')
    delete_module_info_java('/tmp/example_v1')
    delete_module_info_java('/tmp/example_v2')

    run_command(' '.join([
        'python3',
        'src/main/python/run.py',
        '--first-version-path',
        '/tmp/example_v1',
        '--second-version-path',
        '/tmp/example_v2',
        '--iteration',
        '1'
    ]))
