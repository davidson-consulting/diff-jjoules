import argparse

class ArgsUtils():

    def build_parser(self):

        parser = argparse.ArgumentParser()
        parser.add_argument('-f', '--path-first-version', required=True, type=str, help='')
        parser.add_argument('-s', '--path-second-version', required=True, type=str, help='')

        return parser
