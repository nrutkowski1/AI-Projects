#!/usr/bin/env python
# coding: utf-8

# In[799]:


from sklearn.tree import DecisionTreeClassifier
import pandas as pd
import numpy as np
# from sklearn.tree import export_graphviz
# import pydotplus
# import sklearn
# from sklearn.metrics import confusion_matrix
# import matplotlib.pyplot as plt
# from matplotlib import cm
from sklearn.model_selection import train_test_split
from sklearn.model_selection import cross_val_score

data = pd.read_csv('Given/trainDataSet (2).csv')
# to get a row in the data frame
# data.loc[row_number]

# board states
board = []
# winner
winner = []
for row in range(0, len(data.loc[:])):
    
    temp = []
    
    for val in data.loc[row][0:42]:
        
        temp.append(val)
        
    board.append(np.array(temp))
    winner.append(np.array(data.loc[row][42]))

# features
x = []
# outcomes
y = []


for val in range(0, len(data.loc[:])):
    
    ncolumns = 7
    nrows = 6
   #  [rows][columns]
    
    row = np.array(data.loc[val])
    tboard = row[:42].reshape(ncolumns, nrows).T
    
    temp_in = []
    
    # get player value in left bottom corner
    if tboard[0][0] == 1:
        temp_in.append(1)   
    elif tboard[0][0] == 2:
        temp_in.append(2)
    else:
        temp_in.append(0)
        
        
    counter1 = 0
    counter2 = 0
    
    # determine the player with most tokens in center column
    for z in range(0, nrows):
        
        if tboard[z][3] == 1:
            counter1 += 1
        if tboard[z][3] == 2:
            counter2 += 1

    if counter1 > counter2:
        temp_in.append(1)
    elif counter1 < counter2: 
        temp_in.append(2)
    else:
        temp_in.append(0)
        
    # # which player is in the middle bottom row
    # if tboard[0][3] == 1:
    #     temp.append(1)
    # elif tboard[0][3] == 2:
    #     temp.append(2)
    # else:
    #     temp.append(0)

    # which player has most pieces in middle row
    counter1 = 0
    counter2 = 0

    for j in range(0, ncolumns):
        
        if tboard[int(nrows/2)][j] == 1: counter1 += 1
        if tboard[int(nrows/2)][j] == 2: counter2 += 1
            
    if counter1 > counter2:
        temp_in.append(1)
    elif counter1 < counter2: 
        temp_in.append(2)
    else:
        temp_in.append(0) 

# instead of looking just at the middle row (89.9% accuracy)
# switch it to start in the middle of the board
            
    counter1 = 0
    counter2 = 1
    
    # higher score for more token in top center area of board
    for i in range(0, nrows):
        for j in range(0, ncolumns):
            
            if tboard[i][j] == 1: 

                val = j
                if j >= int(ncolumns/2):
                    val = ncolumns - j - 1
                counter1 += val
                val = i
                counter1 += nrows - i
                
            if tboard[i][j] == 2:
                val = j
                if j >= int(ncolumns/2):
                    val = ncolumns - j
                counter2 += val
                counter2 += nrows - i
    if counter1 > counter2:
        temp_in.append(1)
    elif counter1 < counter2: 
        temp_in.append(2)
    else:
        temp_in.append(0)  

#     # count most x in a row
#     counter1 = 1
#     counter2 = 0
    
#     udiaglist = []
#     ldiaglist = []
#     collist = []
#     rowlist = []
#     # upper diag
#     for j in range(1, ncolumns):
#         temp = []
#         for k in range(0, ncolumns):
#             if k < nrows and j + k < ncolumns:
#                 temp.append(tboard[k][j+k])
#         udiaglist.append(temp)

#     for j in range(ncolumns-2, -1, -1):
#         temp = []
#         for k in range(0, ncolumns):
#             if k > -1 and j - k > - 1:
#                 temp.append(tboard[nrows - 1 - k][j-k])
#         udiaglist.append(temp)
#     # lower diag
#     for j in range(ncolumns-2, -1, -1):
#         temp = []
#         for k in range(0, ncolumns):
#             if k < nrows and j - k >-1:
#                 temp.append(tboard[k][j-k])
#         ldiaglist.append(temp)

#     for j in range(1, ncolumns):
#         temp = []
#         for k in range(0, ncolumns):
#             if k < nrows and j + k < ncolumns:
#                 temp.append(tboard[nrows - 1 - k][j+k])
#         ldiaglist.append(temp)
#     # rows 
#     for i in range(0, nrows):
#         rowlist.append(tboard[i][:])
#     # columns    
#     for i in range(0, nrows):
#         temp = []
#         for j in range(0, ncolumns):
#             temp.append(tboard[i][j])
#         collist.append(temp)
        
#     subs1 = [[1, 1, 1, 1], 
#              [1, 1, 1, 0], 
#              [1, 1, 0, 1], 
#              [1, 0, 1, 1], 
#              [0, 1, 1, 1], 
#              [1, 1, 0, 0], 
#              [1, 0, 1, 0], 
#              [0, 1, 1, 0], 
#              [0, 1, 0, 1], 
#              [0, 0, 1, 1], 
#              [0, 0, 0, 1], 
#              [0, 0, 1, 0], 
#              [0, 1, 0, 0],
#              [1, 0, 0, 0], 
#              [1, 1, 1], 
#              [1, 1, 0],
#              [1, 0, 0],
#              [1, 0, 1],
#              [0, 1, 1],
#              [0, 0, 1],
#              [1, 0],
#              [1, 1],
#              [0, 1]]
#     subs2 = [[2, 2, 2, 2], 
#              [2, 2, 2, 0], 
#              [2, 2, 0, 2], 
#              [2, 0, 2, 2], 
#              [0, 2, 2, 2], 
#              [2, 2, 0, 0], 
#              [2, 0, 2, 0], 
#              [0, 2, 2, 0], 
#              [0, 2, 0, 2], 
#              [0, 0, 2, 2], 
#              [0, 0, 0, 2], 
#              [0, 0, 2, 0], 
#              [0, 2, 0, 0],
#              [2, 0, 0, 0], 
#              [2, 2, 2], 
#              [2, 2, 0],
#              [2, 0, 0],
#              [2, 0, 2],
#              [0, 2, 2],
#              [0, 0, 2],
#              [2, 2], 
#              [0, 2], 
#              [2, 0]]
     
#     for directlist in zip(udiaglist, ldiaglist, collist, rowlist):
#         for subs in zip(subs1, subs2):
            
#             if sublist(directlist[0], subs[0]): counter1 += subs[0].count(1)
#             if sublist(directlist[1], subs[0]): counter1 += subs[0].count(1)
#             if sublist(directlist[2], subs[0]): counter1 += subs[0].count(1)
#             if sublist(directlist[3], subs[0]): counter1 += subs[0].count(1)
#             if sublist(directlist[0], subs[1]): counter2 += subs[1].count(2)
#             if sublist(directlist[1], subs[1]): counter2 += subs[1].count(2)
#             if sublist(directlist[2], subs[1]): counter2 += subs[1].count(2)
#             if sublist(directlist[3], subs[1]): counter2 += subs[1].count(2)
                     
#     if counter1 > counter2:
#         temp_in.append(1)
#     elif counter1 < counter2: 
#         temp_in.append(2)
#     else:
#         temp_in.append(0) 
                    
                
    # count player with most nearest neighbors, skew towards player 1 by +1 since for every board states
    # player 1 gets to go next
    counter1 = 1
    counter2 = 0
            
    for z in range(0, len(row) - 1):
        
        if row[z] == 1 or row[z] == 0:
            if (z + 1)%6 == 0:
                if 6 < z + 1 < 42:
                    if row[z - 1] == 1: counter1 += 1
                    if row[z - 6] == 1: counter1 += 1
                    if row[z + 6] == 1: counter1 += 1
                    if row[z - 7] == 1: counter1 += 1
                    if row[z + 5] == 1: counter1 += 1
                elif z+1 == 6:
                    if row[z - 1] == 1: counter1 += 1
                    if row[z + 6] == 1: counter1 += 1
                    if row[z + 5] == 1: counter1 += 1
                elif z+1 == 42:
                    if row[z - 1] == 1: counter1 += 1
                    if row[z - 6] == 1: counter1 += 1
                    if row[z - 7] == 1: counter1 += 1
            elif z%6 == 0:
                if 0 < z + 1 < 42:
                    if row[z + 1] == 1: counter1 += 1
                    if row[z - 6] == 1: counter1 += 1
                    if row[z + 6] == 1: counter1 += 1
                    if row[z - 7] == 1: counter1 += 1
                    if row[z + 5] == 1: counter1 += 1
                elif z == 0:
                    if row[z + 1] == 1: counter1 += 1
                    if row[z + 6] == 1: counter1 += 1
                    if row[z + 5] == 1: counter1 += 1
                elif z == 36:
                    if row[z + 1] == 1: counter1 += 1
                    if row[z - 6] == 1: counter1 += 1
                    if row[z - 7] == 1: counter1 += 1
            elif z == 1 or z == 2 or z == 3 or z == 4:
                if row[z+1] == 1: counter1 += 1
                if row[z-1] == 1: counter1 += 1
                if row[z+6] == 1: counter1 += 1
                if row[z+5] == 1: counter1 += 1
                if row[z+7] == 1: counter1 += 1
            elif z == 37 or z == 38 or z == 39 or z == 40:
                if row[z+1] == 1: counter1 += 1
                if row[z-1] == 1: counter1 += 1
                if row[z-6] == 1: counter1 += 1
                if row[z-7] == 1: counter1 += 1
                if row[z-5] == 1: counter1 += 1
            else:
                if row[z+1] == 1: counter1 += 1
                if row[z-1] == 1: counter1 += 1
                if row[z-6] == 1: counter1 += 1
                if row[z+6] == 1: counter1 += 1
                if row[z-5] == 1: counter1 += 1
                if row[z+5] == 1: counter1 += 1
                if row[z-7] == 1: counter1 += 1
                if row[z+7] == 1: counter1 += 1
        if row[z] == 2 or row[z] == 0:
            if (z + 1)%6 == 0:
                if 6 < z + 1 < 42:
                    if row[z - 1] == 2: counter2 += 1
                    if row[z - 6] == 2: counter2 += 1
                    if row[z + 6] == 2: counter2 += 1
                    if row[z - 7] == 2: counter2 += 1
                    if row[z + 5] == 2: counter2 += 1
                elif z+1 == 6:
                    if row[z - 1] == 2: counter2 += 1
                    if row[z + 6] == 2: counter2 += 1
                    if row[z + 5] == 2: counter2 += 1
                elif z+1 == 42:
                    if row[z - 1] == 2: counter2 += 1
                    if row[z - 6] == 2: counter2 += 1
                    if row[z - 7] == 2: counter2 += 1
            elif z%6 == 0:
                if 0 < z + 1 < 42:
                    if row[z + 1] == 2: counter2 += 1
                    if row[z - 6] == 2: counter2 += 1
                    if row[z + 6] == 2: counter2 += 1
                    if row[z - 7] == 2: counter2 += 1
                    if row[z + 5] == 2: counter2 += 1
                elif z == 0:
                    if row[z + 1] == 2: counter2 += 1
                    if row[z + 6] == 2: counter2 += 1
                    if row[z + 5] == 2: counter2 += 1
                elif z == 36:
                    if row[z + 1] == 2: counter2 += 1
                    if row[z - 6] == 2: counter2 += 1
                    if row[z - 7] == 2: counter2 += 1
            elif z == 1 or z == 2 or z == 3 or z == 4:
                if row[z+1] == 2: counter2 += 1
                if row[z-1] == 2: counter2 += 1
                if row[z+6] == 2: counter2 += 1
                if row[z+5] == 2: counter2 += 1
                if row[z+7] == 2: counter2 += 1
            elif z == 37 or z == 38 or z == 39 or z == 40:
                if row[z+1] == 2: counter2 += 1
                if row[z-1] == 2: counter2 += 1
                if row[z-6] == 2: counter2 += 1
                if row[z-7] == 2: counter2 += 1
                if row[z-5] == 2: counter2 += 1
            else: 
                if row[z+1] == 2: counter2 += 1
                if row[z-1] == 2: counter2 += 1
                if row[z-6] == 2: counter2 += 1
                if row[z+6] == 2: counter2 += 1
                if row[z-5] == 2: counter2 += 1
                if row[z+5] == 2: counter2 += 1
                if row[z-7] == 2: counter2 += 1
                if row[z+7] == 2: counter2 += 1             
    
    if counter1 > counter2:
        temp_in.append(1)
    elif counter1 < counter2: 
        temp_in.append(2)
    else:
        temp_in.append(0)
        
    x.append(np.array(temp_in))
    y.append(row[42])


acc = []
depth = []

best = 0
w1 = 0.5
w2 = 0.5

# for i in range(0, 100):

#     for j in range(0, 100):

#         w11 = 0.5 + (i/100)
#         w22 = 0.5 - (j/100)

x_train, x_test, y_train, y_test = train_test_split(x, y)
tree_clf = DecisionTreeClassifier(criterion='entropy', splitter='best',
                                  max_depth=5, min_samples_split=2, max_features=5,
                                  max_leaf_nodes=None, min_impurity_decrease=0.,
                                  class_weight={1: w1, 2: w2}, presort=True)

print(np.mean(cross_val_score(tree_clf, x, y, cv=10)))
print(cross_val_score(tree_clf, x, y, cv=10))
#         if np.mean(cross_val_score(tree_clf, x, y, cv=10)) >= best:

#             best = np.mean(cross_val_score(tree_clf, x, y, cv=10))
#             w1 = w11
#             w2 = w22

# best, w1, w2

# plt.plot(depth, acc, 'r-')
# plt.xlabel('Max Tree Depth')
# plt.ylabel('Mean Cross Validation Accuracy')
# plt.title('Entropy')
# plt.savefig('entropy.png')
# plt.show()

tree_clf.fit(x_train, y_train)
print(tree_clf.score(x_test, y_test))

#
# dot_data = export_graphviz(tree_clf,
#                            out_file=None,
#                            feature_names=['player with bottom left piece',
#                                           'player with most in center column',
#                                           'player with most in middle row',
#                                           'player with more pieces in middle and towards top of board',
#                                           'player with most nearest neighbors'],
#                            class_names=['Player 1 Wins', 'Player 2 Wins'],
#                            rounded=True,
#                            filled=True)
#
# # Draw graph
# graph = pydotplus.graph_from_dot_data(dot_data)
#
# # Show graph
# Image(graph.create_png())
# # Create PNG
# graph.write_png("test_{}.png".format(tree_clf.score(x_test, y_test)))


print(tree_clf.feature_importances_)

print(tree_clf.score(x_test, y_test))


# In[758]:


y_pred = tree_clf.predict(x_test)


# In[760]:


# %matplotlib inline

# conf_mat = confusion_matrix(y_test, y_pred)
#
# plt.matshow(conf_mat, cmap=cm.binary)
# plt.savefig('conf_mat_94.png')
# conf_mat


# In[ ]:





# In[ ]:



# board[row].reshape(7,6).T

# visualize the board
# # In[630]:
#
#
# get_ipython().run_line_magic('matplotlib', 'inline')
#
# row = np.random.randint(0,1000)
# plt.imshow(board[row].reshape(7,6).T[::-1])
# plt.title("winner = " + str(winner[row]))
# plt.savefig('sample_feature')
#
#
# # In[632]:
#
#
# board[row].reshape(7,6).T[::-1]


# In[ ]:





# In[ ]:


def sublist(lst, sblst):

    dlen = 0
    
    for i in range(0, len(lst) - (len(sblst) - 1)):
        
        if dlen == len(sblst):
                return True
        
        for j in range(0, len(sblst)):
            
            if lst[i + j] == sblst[j]:
                
                dlen += 1

    return False

