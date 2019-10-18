import sys
import csv
import numpy as np

if __name__ == "__main__":

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    # input_file = 'Given//trainDataSet (2).csv'
    # output_file = 'Given//out_test.csv'

    data = list(csv.reader(open(input_file, 'r')))

    # board states
    board = []
    # winner
    winner = []

    for row in range(1, len(data)):

        temp = []

        for val in data[row][0:42]:
            temp.append(val)

        board.append(np.array(temp, dtype=np.int))
        winner.append(np.array(data[42], dtype=np.int))

    # features
    x = ['features']
    # outcomes
    y = []

    for val in range(1, len(data)):

        ncolumns = 7
        nrows = 6
        #  [rows][columns]

        row = np.array(data[val], dtype=np.int)
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

        # which player has most pieces in middle row
        counter1 = 0
        counter2 = 0

        for j in range(0, ncolumns):

            if tboard[int(nrows / 2)][j] == 1:
                counter1 += 1
            if tboard[int(nrows / 2)][j] == 2:
                counter2 += 1

        if counter1 > counter2:
            temp_in.append(1)
        elif counter1 < counter2:
            temp_in.append(2)
        else:
            temp_in.append(0)

        counter1 = 0
        counter2 = 1

        # higher score for more token in top center area of board
        for i in range(0, nrows):
            for j in range(0, ncolumns):

                if tboard[i][j] == 1:

                    val = j
                    if j >= int(ncolumns / 2):
                        val = ncolumns - j - 1
                    counter1 += val
                    val = i
                    counter1 += nrows - i

                if tboard[i][j] == 2:
                    val = j
                    if j >= int(ncolumns / 2):
                        val = ncolumns - j
                    counter2 += val
                    counter2 += nrows - i
        if counter1 > counter2:
            temp_in.append(1)
        elif counter1 < counter2:
            temp_in.append(2)
        else:
            temp_in.append(0)

        # count player with most nearest neighbors, skew towards player 1 by +1 since for every board states
        # player 1 gets to go next
        counter1 = 1
        counter2 = 0

        for z in range(0, len(row) - 1):

            if row[z] == 1 or row[z] == 0:
                if (z + 1) % 6 == 0:
                    if 6<z + 1<42:
                        if row[z - 1] == 1: counter1 += 1
                        if row[z - 6] == 1: counter1 += 1
                        if row[z + 6] == 1: counter1 += 1
                        if row[z - 7] == 1: counter1 += 1
                        if row[z + 5] == 1: counter1 += 1
                    elif z + 1 == 6:
                        if row[z - 1] == 1: counter1 += 1
                        if row[z + 6] == 1: counter1 += 1
                        if row[z + 5] == 1: counter1 += 1
                    elif z + 1 == 42:
                        if row[z - 1] == 1: counter1 += 1
                        if row[z - 6] == 1: counter1 += 1
                        if row[z - 7] == 1: counter1 += 1
                elif z % 6 == 0:
                    if 0<z + 1<42:
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
                    if row[z + 1] == 1: counter1 += 1
                    if row[z - 1] == 1: counter1 += 1
                    if row[z + 6] == 1: counter1 += 1
                    if row[z + 5] == 1: counter1 += 1
                    if row[z + 7] == 1: counter1 += 1
                elif z == 37 or z == 38 or z == 39 or z == 40:
                    if row[z + 1] == 1: counter1 += 1
                    if row[z - 1] == 1: counter1 += 1
                    if row[z - 6] == 1: counter1 += 1
                    if row[z - 7] == 1: counter1 += 1
                    if row[z - 5] == 1: counter1 += 1
                else:
                    if row[z + 1] == 1: counter1 += 1
                    if row[z - 1] == 1: counter1 += 1
                    if row[z - 6] == 1: counter1 += 1
                    if row[z + 6] == 1: counter1 += 1
                    if row[z - 5] == 1: counter1 += 1
                    if row[z + 5] == 1: counter1 += 1
                    if row[z - 7] == 1: counter1 += 1
                    if row[z + 7] == 1: counter1 += 1
            if row[z] == 2 or row[z] == 0:
                if (z + 1) % 6 == 0:
                    if 6<z + 1<42:
                        if row[z - 1] == 2: counter2 += 1
                        if row[z - 6] == 2: counter2 += 1
                        if row[z + 6] == 2: counter2 += 1
                        if row[z - 7] == 2: counter2 += 1
                        if row[z + 5] == 2: counter2 += 1
                    elif z + 1 == 6:
                        if row[z - 1] == 2: counter2 += 1
                        if row[z + 6] == 2: counter2 += 1
                        if row[z + 5] == 2: counter2 += 1
                    elif z + 1 == 42:
                        if row[z - 1] == 2: counter2 += 1
                        if row[z - 6] == 2: counter2 += 1
                        if row[z - 7] == 2: counter2 += 1
                elif z % 6 == 0:
                    if 0<z + 1<42:
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
                    if row[z + 1] == 2: counter2 += 1
                    if row[z - 1] == 2: counter2 += 1
                    if row[z + 6] == 2: counter2 += 1
                    if row[z + 5] == 2: counter2 += 1
                    if row[z + 7] == 2: counter2 += 1
                elif z == 37 or z == 38 or z == 39 or z == 40:
                    if row[z + 1] == 2: counter2 += 1
                    if row[z - 1] == 2: counter2 += 1
                    if row[z - 6] == 2: counter2 += 1
                    if row[z - 7] == 2: counter2 += 1
                    if row[z - 5] == 2: counter2 += 1
                else:
                    if row[z + 1] == 2: counter2 += 1
                    if row[z - 1] == 2: counter2 += 1
                    if row[z - 6] == 2: counter2 += 1
                    if row[z + 6] == 2: counter2 += 1
                    if row[z - 5] == 2: counter2 += 1
                    if row[z + 5] == 2: counter2 += 1
                    if row[z - 7] == 2: counter2 += 1
                    if row[z + 7] == 2: counter2 += 1

        if counter1>counter2:
            temp_in.append(1)
        elif counter1<counter2:
            temp_in.append(2)
        else:
            temp_in.append(0)
        x.append(np.array(temp_in))
        y.append(row[42])

    outfilepath = "./" + str(output_file)
    outfile = open(outfilepath, 'w', newline='')
    writer = csv.writer(outfile, delimiter=',')

    out = []

    for i in zip(data, x):

        temp = []
        for j in i[0]:
            temp.append(j)
        temp.append(i[1])

        out.append(temp)

    for row in out:
        writer.writerow(row)
