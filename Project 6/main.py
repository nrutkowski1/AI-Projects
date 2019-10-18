
import sys
from node2 import*
import networkx as nx
# import matplotlib.pyplot as plt
import random
# import numpy as np


# read the network file and return a list of nodes in the file
def make_nodes(net_file):
    # open the file with node data
    with open(net_file, 'r') as nfile:
        # read all of the lines of the file
        lines = nfile.readlines()
        # list of nodes created
        node_list = []
        # iterate over each line with node information in the file
        for line in lines:
            # processing the text
            line = line.replace('\n', '')
            line = line.split(':')
            # name of the node
            name = line[0]

            line_ = line[1].split('] [')
            # check if there are parent nodes and/or get the node names
            if len(line_[0].replace(' [', '')) > 0:
                parent_names = line_[0].replace(' [', '').split(' ')
            else:
                parent_names = []
            # create a list of the probabilities
            probs_table = [float(i) for i in line_[1].replace(']', '').split(' ')]
            # make the nodes and add them to the list of nodes
            node_list.append(Node(name, parent_names, probs_table, 0, -1))
        # iterate over each node
        for node in node_list:
            new_parents = []
            # iterate over the parents names in the node
            for parents_name in node.get_parents():
                for parent_node in node_list:
                    # if a nodes parent is a nodes name
                    if parents_name == parent_node.get_name():
                        # add the node to the parents list of each node
                        new_parents.append(parent_node)

            node.set_parents(new_parents)

    return node_list


# get the node type data from the query node
def define_node_type(nodes, queryfile):
    # open the file with query data
    with open(queryfile, 'r') as qfile:
        # read the line in the file and process the text
        line = qfile.read()
        line = line.replace(',', '')
        # set the type of the nodes based on query data types defined in node class
        for node in zip(nodes, line):
            if node[1] == '?':
                node[0].set_type(1)
            elif node[1] == 't':
                node[0].set_type(2)
            elif node[1] == 'f':
                node[0].set_type(3)
            elif node[1] == '-':
                node[0].set_type(4)

    return nodes


# make the bayesian network
def make_bayes_net(node_list):
    # create a graph
    graph = nx.MultiDiGraph()
    # add each node to the graph
    for node in node_list:
        graph.add_node(node)
    # iterate over all of the nodes
    for node in node_list:
        # iterate over each nodes parents
        for parent in node.get_parents():
            # add an edge from the parent to the node
            graph.add_edge(parent, node)

    return graph


# do rejection sampling of graph on given number of samples
def rejection_sampling(num_samples, graph, query_node):

    # keep track of number of samples
    num_not_discarded_q_true = 1
    num_not_discarded = 1

    # recursively update the estimate for the number of defined samples
    for i in range(0, num_samples):
        ps = prior_sample(graph)
        # check if the sample is consistent
        if check_consistent(ps, graph):
            num_not_discarded += 1
            # check if the sample is type true
            if ps[query_node.get_name()] == 2:
                num_not_discarded_q_true += 1
    # return the probability based on the number of true query samples and not discarded and the number
    # of samples not discarded
    return num_not_discarded_q_true / num_not_discarded


# get the prior sample based on pseudo code from the textbook
def prior_sample(graph):

    # sort the bayesian network using a topological graph sort
    sorted_graph = nx.topological_sort(graph)

    # dict for the sample with key = node name and value = node type)
    sample = {}

    # generate a random sample for each node in the graph
    for node in sorted_graph:
        random_sample(node, sample)

    return sample


# randomly sample the bayesian network
def random_sample(node, sample):
    # random sample between 0 and 1
    rand_sample = random.random()

    # if a node has no parents get the second value in the conditional probability table as defined in the project
    # description
    if len(node.get_parents()) == 0:
        probs = node.get_cpt()[1]

        # give the node a true or false value depending on if the randomly generated number is less than or greater
        # than the value from the conditional probability table
        if rand_sample <= probs:
            sample[node.name] = 2
        else:
            sample[node.name] = 3

    # if the node has 1 parent
    elif len(node.get_parents()) == 1:
        # get the name of the parent
        parent_name = node.get_parents()[0].get_name()
        # if the parent is type true get the 4th value in the conditional probability or get the 2nd value in the
        # conditional probability table if the type is false as defined in the project
        if sample[parent_name] == 2:
            probs = node.get_cpt()[3]
        else:
            probs = node.get_cpt()[1]

        # give a true or false value to the random sample depending on if the random sampled value is less than
        # or greater than the probability value for the nodes parent
        if rand_sample <= probs:
            sample[node.get_name()] = 2
        else:
            sample[node.get_name()] = 3

    # check if a parent has 2 nodes
    elif len(node.get_parents()) == 2:
        # get the names of the parents of the node
        p1 = node.get_parents()[0].get_name()
        p2 = node.get_parents()[1].get_name()

        # get the value from the conditional probability table of the node depending on which parents are true as
        # defined in the project
        if sample[p1] == 2 and sample[p2] == 3:
            probs = node.get_cpt()[1]
        elif sample[p1] == 3 and sample[p2] == 2:
            probs = node.get_cpt()[3]
        elif sample[p1] == 2 and sample[p2] == 3:
            probs = node.get_cpt()[5]
        else:
            probs = node.get_cpt()[7]

        # give a true or false type based on the conditional probability tables and the random sample
        if rand_sample <= probs:
            sample[node.get_name()] = 2
        else:
            sample[node.get_name()] = 3

    return sample


# Perform likelihood sampling on a given number of samples
def likelihood_sampling(num_samples, graph, qnode):

    true_samples = 1
    weighted_value = 1
    # recursively update estimate a defined number of times
    for i in range(0, num_samples):
        # get a weighted sample
        sample, weight = weighted_sample(graph)
        # sum values for total probability
        if sample[qnode.name] == 2:
            true_samples += weight
        weighted_value += weight

        plot_vals.append(true_samples/weighted_value)

    return true_samples/weighted_value


# get a weight sample from the bayesian network
def weighted_sample(graph):

    sample = {}
    # topologically sort the bayesian network
    sorted_graph = nx.topological_sort(graph)

    # iterate over sorted nodes
    for node in sorted_graph:

        rand_val = random.random()
        weight = 1.0

        # If node has no parents
        if len(node.get_parents()) == 0:
            probs = node.get_cpt()[1]

        # If node has 1 parent
        elif len(node.get_parents()) == 1:
            parent = node.get_parents()[0]
            if parent.get_type() == 2:
                probs = node.get_cpt()[3]
            else:
                probs = node.get_cpt()[1]

        # If node has 2 parents
        elif len(node.get_parents()) == 2:
            p1 = node.get_parents()[0]
            p2 = node.get_parents()[1]

            if p1.get_type() == 2 and p2.get_type() == 3:
                probs = node.get_cpt()[1]
            elif p1.get_type() == 3 and p2.get_type() == 2:
                probs= node.get_cpt()[3]
            elif p1.get_type() == 2 and p2.get_type() == 3:
                probs = node.get_cpt()[5]
            else:
                probs = node.get_cpt()[7]

        # Given parent probability, sample and weight
        if node.get_type() == 2:
            sample[node.get_name()] = node.get_type()
            weight = weight * probs
        elif node.get_type() == 3:
            sample[node.get_name()] = node.get_type()
            weight = weight * (1 - probs)
        else:
            if rand_val <= probs:
                sample[node.get_name()] = 2
            else:
                sample[node.get_name()] = 3

    return sample, weight


# get the query node that is in the list of nodes
def find_query(nodes):

    for node in nodes:
        if node.get_type() == 1:
            return node


# check if a given sample is consistent with the graph
def check_consistent(sample, node_list):

    temp_node = -1
    # iterate over dict of samples
    for sample_name in sample:
        # get complete node
        for node in node_list.nodes():
            if node.get_name() == sample_name:
                temp_node = node
        # reject a sample
        if temp_node.get_type() == 2:
            if sample[sample_name] == 3:
                return False
        elif temp_node.get_type() == 3:
            if sample[sample_name] == 2:
                return False

    return True


# get files
network_file = sys.argv[1]
query_file = sys.argv[2]
number_of_samples = sys.argv[3]
# create the nodes
nodes_list = make_nodes(network_file)
# add the types ot the node
assigned_nodes = define_node_type(nodes_list, query_file)
# create the bayesian network as a graph
g = make_bayes_net(assigned_nodes)
# find the query node
query = find_query(assigned_nodes)
plot_vals = []


# Perform rejection sampling on Bayesian Network, return value
rejection_sampling_results = rejection_sampling(number_of_samples, g, query)

# Perform likelihood weighting sampling on Bayesian Network, return value
like_sampling_results = likelihood_sampling(number_of_samples, g, query)

print("Rejection Sampling Results {}".format(rejection_sampling_results))
print("Likelihood Weighting Results {}".format(like_sampling_results))
#
# network_file = 'network_option_a.txt'
# query_files = ['query1.txt', 'query2.txt']
# samples_list = [200, 400, 600, 800, 1000, 1500, 2000, 3000, 4000, 5000]
# trials = 10
# nodes_list = make_nodes(network_file)
#
#
# for qfile in query_files:
#
#     assigned_nodes = define_node_type(nodes_list, qfile)
#     g = make_bayes_net(assigned_nodes)
#     query = find_query(assigned_nodes)
#
#     mean_rs = []
#     var_rs = []
#     mean_ls = []
#     var_ls = []
#
#     for nums in samples_list:
#
#         rs = [rejection_sampling(nums, g, query) for i in range(0, 10)]
#         ls = [likelihood_sampling(nums, g, query) for j in range(0, 10)]
#
#         mean_rs.append(np.mean(rs))
#         var_rs.append(np.var(rs))
#         mean_ls.append(np.mean(ls))
#         var_ls.append(np.var(ls))
#
#     plt.plot(samples_list, mean_rs)
#     plt.plot(samples_list, mean_ls)
#     plt.legend(['Rejection Sampling', 'Likelihood Sampling'])
#     plt.xlabel('Number of Samples')
#     plt.ylabel('Mean')
#     plt.title('{} Mean'.format(qfile.replace('.txt', '')))
#     plt.savefig('mean_new_{}.png'.format(qfile.replace('.txt', '')))
#     plt.show()
#
#     plt.plot(samples_list, var_rs)
#     plt.plot(samples_list, var_ls)
#     plt.legend(['Rejection Sampling', 'Likelihood Sampling'])
#     plt.xlabel('Number of Samples')
#     plt.ylabel('Variance')
#     plt.title('{} Variance'.format(qfile.replace('.txt', '')))
#     plt.savefig('var_new_{}.png'.format(qfile.replace('.txt', '')))
#     plt.show()

# plt.hist(plot_vals, bins=50)
# plt.show()
# visualize the bayesian network
# names = {}
# for n in nodes_list:
#     names[n] = n.get_name()
# pos = nx.spring_layout(g)
# nx.draw_networkx_nodes(g, pos, cmap=plt.get_cmap('jet'), node_size=500)
# nx.draw_networkx_labels(g, pos, labels=names, font_size=20)
# nx.draw_networkx_edges(g, pos, edge_color='r', arrows=True)
# nx.draw_networkx_edges(g, pos, arrows=True)
# plt.show()
