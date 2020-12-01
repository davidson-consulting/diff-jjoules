import argparse

class RunArgs():

    def build_parser(self):

        parser = argparse.ArgumentParser()
        parser.add_argument('-p', '--project-name', type=str, help='Specify the name of the project. Will be used for output purpose')
        parser.add_argument('-o', '--output', type=str, help='Specify the path to the ouput folder. Should exists.')
        parser.add_argument('-f', '--sha-v1', type=str, help='Specify the commit sha of the first version')
        parser.add_argument('-s', '--sha-v2', type=str, help='Specify the commit sha of the second version')
        parser.add_argument('-i', '--iteration', type=str, help='Specify the number of iteration be done', default='2')

        return parser
