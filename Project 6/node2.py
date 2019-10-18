
# node types
# query = 1
# evidence true = 2
# evidence false = 3
# unknown = 4


class Node:
    def __init__(self, name, parents, cpt, type_, value):
        self.name = name
        self.parents = parents
        self.cpt = cpt
        self.type = type_
        self.value = value

    def set_type(self, num): self.type = num
    def get_type(self): return self.type
    def get_parents(self): return self.parents
    def set_parents(self, par_list): self.parents = par_list
    def get_name(self): return self.name
    def get_cpt(self): return self.cpt


