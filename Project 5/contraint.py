


class Constraint():
    def __init__(self):
        self.x = 0
        self.y = 0
        self.unary_inclusive = {}
        self.unary_exclusive = {}
        self.binary_equals = []
        self.binary_not_equals = []
        self.binary_simultaneous = {}
