# # Project 5 - CSP
# # Theresa Inzerillo & Preston Mueller
# # CS4341 Introduction to Artificial Intelligence
# #
# # Bag object class
#

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


# class Bag:
#     def __init__(self, name, capacity):
#         self._name = name
#         self._capacity = capacity
#         self._weight = 0
#         self.items = []
#
#     @property
#     def name(self):
#         return self._name
#
#     @property
#     def capacity(self):
#         return self._capacity
#
#     @property
#     def weight(self):
#         return self._weight
#
#     @property
#     def contains(self):
#         return self._contains
#
#     def wastedCapacity(self):
#         return self.capacity - self.weight
#
#     def addItem(self, type, number):
#         if type not in self._contains:
#             self._contains.append(type)
#         self._weight += number
#
#     def removeItem(self, type, number):
#         if type in self.contains:
#             self.contains.remove(type)
#         self._weight -= number
