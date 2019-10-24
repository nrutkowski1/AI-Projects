
class Bag:

    def __init__(self, name, capacity):

        self.name = name
        self.capacity = capacity
        self.weight = 0
        self.items = []

    def get_name(self): return self.name

    def get_capacity(self): return self.capacity

    def get_weight(self): return self.weight

    def get_items(self): return self.items

    def add_item(self, item, weight):

        if item not in self.items:

            self.items.append(item)
            self.weight += weight

    def remove_item(self, item, weight):

        if item in self.items:

            self.items.remove(item)
            self.weight -= weight

    def space(self): return self.capacity - self.weight



