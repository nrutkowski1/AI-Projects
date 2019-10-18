from contraint import Constraint
import sys
from time import time
from bag import *
import math as m
import os

# Global variables
items = {}
bags = []
c = Constraint()
counter = 0

######################## INPUT FILE READER ########################
def load_file(file):
    global items, bags, c

    # Booleans for reading in constraints.
    set_var = False
    set_val = True
    set_limits = True
    set_unary_ex = True
    set_unary_in = True
    set_binary_equals = True
    set_binary_not_equals = True
    set_binary_simultaneous = True

    # Open file
    input_open = open(file, 'r')

    # Get the total line count for the input file.
    line_read_count = input_open.readline()
    line_count = 0
    while line_read_count:
        line_count += 1
        line_read_count = input_open.readline()
    input_open.close()

    # Re-open file and read line-by-line, assigning inputs to constraints
    progress_count = 0
    input_open = open(file, 'r')
    line_read = input_open.readline()
    while line_read:
        # As long as line is not a comment line
        if line_read[0] == '#':

            # Set items
            if not set_var:
                line_read = input_open.readline()
                progress_count += 1
                while line_read[0] != '#':
                    items[line_read[0]] = int(line_read[2:])
                    line_read = input_open.readline()
                    progress_count += 1
                set_var = True
                set_val = False

            # Set bags
            if not set_val:
                line_read = input_open.readline()
                progress_count += 1
                while line_read[0] != '#':
                    bags.append(line_read)
                    line_read = input_open.readline()
                    progress_count += 1
                set_val = True
                set_limits = False

            # Set limits
            if not set_limits:
                line_read = input_open.readline()
                progress_count += 1
                while line_read[0] != '#':
                    c.x = int(line_read[0])
                    c.y = int(line_read[2])
                    line_read = input_open.readline()
                    progress_count += 1
                set_limits = True
                set_unary_in = False

            # Set unary inclusive
            if not set_unary_in:
                line_read = input_open.readline()
                progress_count += 1
                while line_read[0] != '#':
                    c.unary_inclusive[line_read[0]] = line_read[1:]
                    # c.unary_inclusive.append(line_read)
                    line_read = input_open.readline()
                    progress_count += 1
                set_unary_in = True
                set_unary_ex = False

            # Set unary exclusive
            if not set_unary_ex:
                line_read = input_open.readline()
                progress_count += 1
                while line_read[0] != '#':
                    c.unary_exclusive[line_read[0]] = line_read[1:]
                    line_read = input_open.readline()
                    progress_count += 1
                set_unary_ex = True
                set_binary_equals = False

            # Set binary equals
            if not set_binary_equals:
                line_read = input_open.readline()
                progress_count += 1
                while line_read[0] != '#':
                    c.binary_equals.append(line_read[0] + line_read[2])
                    line_read = input_open.readline()
                    progress_count += 1
                set_binary_equals = True
                set_binary_not_equals = False

            # Set binary not equal
            if not set_binary_not_equals:
                line_read = input_open.readline()
                progress_count += 1
                while line_read[0] != '#':
                    c.binary_not_equals.append(line_read[0] + line_read[2])
                    line_read = input_open.readline()
                    progress_count += 1
                set_binary_not_equals = True
                set_binary_simultaneous = False

            # Set binary simultaneous
            if not set_binary_simultaneous:
                line_read = input_open.readline()
                progress_count += 1
                if progress_count == line_count:
                    break
                while line_read[0] != '#':
                    c.binary_simultaneous[line_read[0] + line_read[2]] = line_read[4] + line_read[6]
                    line_read = input_open.readline()
                    progress_count += 1
                    # Make sure program doesn't read off EOF.
                    if progress_count == line_count:
                        break
                set_binary_simultaneous = True

        line_read = input_open.readline()
    input_open.close()


    # Reassign bags
    temp_bags = []
    for bag in bags:
        temp_bags.append(Bag(bag[0], int(bag[2:])))
    bags = temp_bags



######################## HELPER FUNCTIONS ########################

# Check if an item is in a bag
def is_in_bag(item, bags_list):
    for bag in bags_list:
        if str(item) in str(bag.items):
            return True
    return False


# Helper for dictionary sort
def unary_weight(x):
    return -1*items[x]


# Check if the number of items in the bag fits within the given constraints for how many items can be in a bag
def in_limits(bag, n): return c.y >= len(bag.get_items()) + n >= c.x


# Determine the most constrained variable:
#       is based on the unary inclusive and exclusive constraints
def check_unary(x):
    return_val = 0
    for val in c.unary_inclusive.keys():
        if str(x) is str(val):
            return_val -= 1
    for val in c.unary_exclusive.keys():
        if str(x) is str(val):
            return_val -= 1
    return return_val


# Get the next most constrained variable that has not been assigned yet to a bag.
def select_unassigned_variable(bags_list):
    vars_ = list(items.keys())
    for item in list(items.keys()):
        if is_in_bag(item, bags_list):
            vars_.remove(item)
            if len(vars_) == 0:
                return []
    return mrv_heuristic(vars_, bags_list)


######################## SEARCHES AND HUERISTIC FUNCTIONS ########################


# Choose variables with the fewest legal moves first.
def mrv_heuristic(var, bags_list):
    temp_bags = {}
    for v in var:
        temp_bags[v] = 0
        for bag in bags_list:
            if forward_check(v, bag, bags_list):
                temp_bags[v] += 1
    sorted_bags = sorted(temp_bags, key=lambda x: (temp_bags.get, check_unary(x), unary_weight(x)))
    return sorted_bags[0]


# Delete inconsistant values from associated variables of chosen variable - prune
def forward_check(v, bag, bags_list):

    # Unary inclusive
    if v in list(c.unary_inclusive.keys()):
        if bag.get_name() not in c.unary_inclusive[v]:
            return False

    # Unary exclusive
    if v in list(c.unary_exclusive.keys()):
        if bag.get_name() in c.unary_exclusive[v]:
            return False

    # Binary equals
    for set_ in c.binary_equals:
        if set_[0] is v:
            if set_[1] not in bag.get_items() and is_in_bag(set_[1], bags_list):
                return False
        elif set_[1] is v:
            if set_[0] not in bag.get_items() and is_in_bag(set_[0], bags_list):
                return False

    # Binary not equals
    for set_ in c.binary_not_equals:
        if set_[0] is v and set_[1] in bag.get_items():
            return False
        elif set_[1] is v and set_[0] in bag.get_items():
            return False

    # Binary simulatenous
    for key in list(c.binary_simultaneous.keys()):
        if key[0] is v and bag.get_name() in c.binary_simultaneous[key]:
            if key[1] in bag.get_items():
                return False
        elif key[1] is v and bag.get_name() in c.binary_simultaneous[key]:
            if key[0] in bag.get_items():
                return False

    # Check the fitting limits constraints
    if c.y is not 0:
        if len(bag.get_items()) + 1 > c.y:
            return False
    return bag.space() >= items[v]


# Decide order to examine domain values for a variable
def lcv_heuristic(var, bags_list):
    temp_list = {}

    for bag in bags_list:
        temp_list[bag] = 0
        for item in var:
            if forward_check(item, bag, bags_list):
                temp_list[bag] += 1

    sorted_bags = sorted(temp_list, key= lambda x: (temp_list.get, degree_heuristic(x)))
    return reversed(sorted_bags)


# Heuristic which returns a value depending on the capacity of the bag and the number of items in the bag
def degree_heuristic(x):
    return_val = 0
    mw = m.floor(x.get_capacity() * 0.9)

    # If capacity is less than 90%
    if x.get_weight() < mw:
        return_val = return_val + (mw - x.get_weight())

    # If upper limit is > 0
    if c.y != 0:
        if len(x.get_items()) < c.x:
            return_val = return_val + (c.x - len(x.get_items()))

    return return_val


# Perform backtrack algorithm on the list of bags from the input
def backtrack(bags_list):
    # used to count the numnber of states visited
    global counter
    counter += 1

    # PERFORM BACKTRACKING WITHOUT ANY ADDITIONAL HEURISTICS
    # temp_list = items
    # # for item in items:
    # #     print(item)
    # for item in temp_list:
    #     for bag in bags_list:
    #         bag.add_item(item, items[item])
    #         removed_item_name = item
    #         removed_item_val = temp_list.get(item)
    #         temp_list.pop(item)
    #         backtrack(bags_list)
    #         if csp_complete(bags_list):
    #             return bags_list
    #         else:
    #             temp_list[removed_item_name] = removed_item_val
    #             bag.remove_item(item, items[item])


    # # PERFORM BACKTRACKING WITH HERUISTICS, WITHOUT FORWARD CHECKING
    # if len(select_unassigned_variable(bags_list)) == 0:
    #     return None
    # var = select_unassigned_variable(bags_list)
    # for val in lcv_heuristic(var, bags_list):
    #     val.add_item(var, items[var])
    #     backtrack(list(bags_list))
    #
    #     if csp_complete(bags_list):
    #         return bags_list
    #     else:
    #         val.remove_item(var, items[var])


    # PERFORM BACKTRACKING + FORWARD-CHECKING + MRV + LCV + DEGREE
    # If there are no items left to place, but csp is not complete, return no solution
    if len(select_unassigned_variable(bags_list)) == 0:
        return None

    var = select_unassigned_variable(bags_list)

    # Call LCV and iterate over
    for val in lcv_heuristic(var, bags_list):
        # Forward check current potential assignment
        if forward_check(var, val, bags_list):
            # Accept assignment
            val.add_item(var, items[var])
            # Recursively call backtrack
            backtrack(list(bags_list))
            # Return if complete
            if csp_complete(bags_list):
                return bags_list
            else:
                val.remove_item(var, items[var])


# Check is the input is a CSP complete problem based on the variables and constraints given.
def csp_complete(bags_list):
    global c

    # Check that all of the items are in a bag
    for item in items:
        # Return false if an item is not in a bag
        if not is_in_bag(item, bags_list):
            return False

    # Check that for each bag the weight is within the constraints and number of items in a bag is within the
    # Fitting limits
    for bag in bags_list:
        # If the bag weight is less that 90% capacity weight or bag weight is greater than the capacity
        # Weight return false
        if bag.get_weight() < m.floor(bag.get_capacity() * 0.9) or bag.get_weight() > bag.get_capacity():
            return False
        # Check that the number of items in a bag is within the fitting limits
        if c.y != 0 and not in_limits(bag, 0):
            return False

    # Check the unary inclusive constraints
    # Iterate over each bag in the inclusive constraints
    for a in c.unary_inclusive.items():
        # Get the corresponding variable of the constraint
        var = a[0]
        temp_bag = None

        for bag in bags_list:
            # Get the bag containing the current item in the constraint
            if var in bag.get_items():
                temp_bag = bag
                break
        # If the bag name is not in the same inclusive constraint definition as the item then the item is in a bag that
        # Does not satisfy the unary inclusive constraint and is not CSP complete so return false
        if temp_bag.get_name() not in a[1]:
            return False

    # Check the unary exclusive constraints
    # Iterate over each of the bags in the unary exclusive constraint
    for a in c.unary_exclusive.items():
        # Get the corresponding variable for the constraint
        var = a[0]
        temp_bag = None
        # Iterate over the list of bags
        for bag in bags_list:
            # Check if the variable in the constraint is any of the variables in any of the bags
            if var in bag.get_items():
                temp_bag = bag
                break
        # Check if the bag containing the item is any of the bags listed in the items exclusive constraint.
        # If it is in it then return false as that item is not excluded from the bag listed and the exclusive
        # constraint is not satisfied.
        if temp_bag.name in a[1]:
            return False

    # Check the binary equals constraint
    for a in c.binary_equals:
        v1 = a[0]
        v2 = a[1]
        for bag in bags_list:
            if v1 in bag.get_items() and v2 not in bag.get_items():
                return False

    # Binary not equal
    for a in c.binary_not_equals:
        v1 = a[0]
        v2 = a[1]
        for bag in bags_list:
            if v1 in bag.get_items() and v2 in bag.get_items():
                return False

    # Binary simultaneous
    for a in c.binary_simultaneous.items():
        v1 = a[0][0]
        v2 = a[0][1]
        b1 = a[1][0]
        b2 = a[1][1]

        temp_b1 = None
        temp_b2 = None

        for bag in bags_list:
            if str(bag.get_name()) == str(b1):
                temp_b1 = bag
            elif str(bag.get_name()) == str(b2):
                temp_b2 = bag

        if v1 in temp_b1.get_items() and v2 not in temp_b2.get_items(): return False
        if v1 in temp_b2.get_items() and v2 not in temp_b1.get_items(): return False
        if v2 in temp_b1.get_items() and v1 not in temp_b2.get_items(): return False
        if v2 in temp_b2.get_items() and v1 not in temp_b1.get_items(): return False

    return True



######################## FILE OUTPUT ########################

def output(out, name):

    with open('output_for_' + name + 'txt', 'w+') as file:
        for o in range(0, len(out)):
            bag = out[o]
            print(bag.get_name(), "", end="")
            file.write('{}'.format(bag.get_name()))
            for var in bag.get_items():
                print(var, "", end="")
                file.write(' {}'.format(var))
            print(" ")
            print('number of items: {}'.format(len(bag.get_items())))
            file.write('\nnumber of items: {}'.format(len(bag.get_items())))
            print('total weight: {}/{}'.format(bag.get_weight(), bag.get_capacity()))
            file.write('\ntotal weight: {}/{}'.format(bag.get_weight(), bag.get_capacity()))
            print('wasted capacity: {}'.format(bag.space()))
            file.write('\nwasted capacity: {}'.format(bag.space()))
            print('\n')
            file.write('\n\n')

        file.close()

######################## PROGRAM EXECUTION ########################

# Timer
start_time = time()

# Take in command line arguments
folder_in = sys.argv[1]
# multi = int(sys.argv[2])

# If running a single input file
# if multi == 0:

# Redefine gloabl variables
# items = {}
# bags = []
# c = Constraint()
file = folder_in
# counter = 0

# File loading
load_file(file)
name = file[:len(file) - 3]

# Run program
final = backtrack(bags)
# print("Number of states visited = ", counter)

end_time = time()
# print("Total time = ", end_time - start_time)

# If solution exists
if final is not None:
    output(final, name)
# If no solution exists
else:
    with open('output_for_' + name + 'txt', 'w+') as f:
        f.write("No solution found")
        f.close()

    print("No solution found")

#
# # If running multiple files in a directory
# if multi == 1:
#
#     # Iterate over all files
#     for file in os.listdir(folder_in):
#
#         # Redefine global variables
#         items = {}
#         bags = []
#         c = Constraint()
#
#         # File laoding, checking
#         if file.__contains__('.txt'):
#             load_file(folder_in + '\\' + file)
#             name = file[:len(file) - 3]
#
#             # Run program
#             final = backtrack(bags)
#
#             # If solution exists
#             if final is not None:
#                 output(final, name)
#             # If no solution exists
#             else:
#                 with open('output_for_' + name + 'txt', 'w+') as f:
#                     f.write("No solution found")
#                     f.close()
#
#                 print("No solution found")
#         else:
#             print('File must be in format .txt')

