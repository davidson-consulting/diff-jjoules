import argparse

class RunArgs():

    def build_parser(self):

        parser = argparse.ArgumentParser()
        parser.add_argument('-f', '--first-version-path', type=str, help='Specify the path to the folder of the first version of the program, i.e. before the commit.')
        parser.add_argument('-s', '--second-version-path', type=str, help='Specify the path to the folder of the second version of the program, i.e. after the commit.')
        parser.add_argument('-i', '--iteration', type=int, help='Specify the number of time tests will be executed for measuring the energy consumption.')

        return parser
