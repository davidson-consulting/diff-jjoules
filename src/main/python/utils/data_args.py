import argparse

class RunArgs():

    def build_parser(self):

        parser = argparse.ArgumentParser()
        parser.add_argument('-p', '--project-name', type=str, help='Specify the name of the project. Will be used for output purpose')
        parser.add_argument('-d', '--data-path', type=str, help='Specify the path to the data folder.')

        return parser
