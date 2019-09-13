
import numpy as np
import sklearn
import matplotlib as mpl
from matplotlib import pyplot as plt
from keras.models import Sequential
from keras.layers import Dense, InputLayer, Dropout
import keras
import tensorflow as tf
from sklearn.metrics import confusion_matrix
from sklearn.model_selection import cross_val_score
from keras.utils import to_categorical
import time

# load data
images = np.load('images.npy')
labels = np.load('labels.npy')

# show image
# plt.imshow(images[0], cmap=mpl.cm.binary)
# plt.show()
print(labels[0])


# In[4]:

# flatten the images
images_flatten = np.array([image.flatten() for image in images])
# convert the labels to vectors
outputs = to_categorical(labels)

batch_size = 512
nodes = 50
epochs = 500
learn_rate = 0.001
hidden_layers = 10

# model without dropout layer used actiavtion functions from tutorials. It was not specified what to use in the homework
model = Sequential()
model.add(Dense(nodes, activation='relu', input_shape=images_flatten[0].shape))

for i in range(0, hidden_layers):
    
    model.add(Dense(nodes, activation='relu'))
    
model.add(Dense(len(outputs[0]), activation='softmax'))

# used sgd optimizer with given learning rate and momentum of 0.5 which was not specified in the homework
sgd = keras.optimizers.SGD(lr=learn_rate, momentum=0.5, nesterov=False)

model.compile(loss='categorical_crossentropy', optimizer=sgd, metrics=['accuracy', 'mse'])

# split up data into training, testing, validation data sets
x_train = images_flatten[:int(0.6*len(images_flatten))]
x_test = images_flatten[int(0.6*len(images_flatten)):int(0.8*len(images_flatten))]
x_validate = images_flatten[int(0.8*len(images_flatten)):len(images_flatten)]

y_train = outputs[:int(0.6*len(outputs))]
y_test = outputs[int(0.6*len(outputs)):int(0.8*len(outputs))]
y_validate = outputs[int(0.8*len(outputs)):len(outputs)]


# show image
# plt.imshow(x_train[10].reshape(28,28), cmap=mpl.cm.binary)
# plt.show()
print(y_train[10])

history = model.fit(x_train, y_train, epochs=epochs, batch_size=batch_size, validation_data=(x_validate, y_validate))

print(history.history.keys())

# plot model accuracy and error without dropout layer

# epoch_list = np.linspace(1,500, 500)
# plt.plot(epoch_list, history.history['acc'], 'b-')
# plt.plot(epoch_list, history.history['val_acc'], 'r-')
#
# plt.xlabel('Epoch Number')
# plt.ylabel('Accuracy')
# plt.title('Model 1 Accuracy')
# plt.legend(['Training Accuracy', 'Validation Accuracy'])
# # plt.savefig('m1acc_hiddenlayers{}.png'.format(hidden_layers))
# plt.show()
#
#
# plt.plot(epoch_list, history.history['mean_squared_error'], 'b-')
# plt.plot(epoch_list, history.history['val_mean_squared_error'], 'r-')
#
# plt.xlabel('Epoch Number')
# plt.ylabel('Mean Squared Error')
# plt.title('Model 1 Error')
# plt.legend(['Training Error', 'Validation Error'])
# # plt.savefig('m1err_hiddenlayers{}.png'.format(hidden_layers))
# plt.show()


# get confusion matrix
predictions = model.predict(x_test)

y_pred = predictions

conf_mat = confusion_matrix(y_test.argmax(axis=1), y_pred.argmax(axis=1))
# plot confusion matrix
# plt.matshow(conf_mat, cmap=mpl.cm.binary)
# plt.xlabel('Actual')
# plt.ylabel('Predictions')
# plt.title('Model 1 Confusion Matrix')
# # plt.savefig('m1confmat_hiddenlayers{}_batch{}.png'.format(hidden_layers, batch_size))
# plt.show()


# model with dropout layer
model2 = Sequential()
model2.add(Dropout(0.2, input_shape=images_flatten[0].shape))
for i in range(0, hidden_layers):
    
    model2.add(Dense(nodes, activation='relu'))
    
model2.add(Dense(len(outputs[0]), activation='softmax'))

sgd = keras.optimizers.SGD(lr=learn_rate, momentum=0.5, nesterov=False)

model2.compile(loss='categorical_crossentropy', optimizer=sgd, metrics=['accuracy', 'mse'])

x_train = images_flatten[:int(0.6*len(images_flatten))]
x_test = images_flatten[int(0.6*len(images_flatten)):int(0.8*len(images_flatten))]
x_validate = images_flatten[int(0.8*len(images_flatten)):len(images_flatten)]

y_train = outputs[:int(0.6*len(outputs))]
y_test = outputs[int(0.6*len(outputs)):int(0.8*len(outputs))]
y_validate = outputs[int(0.8*len(outputs)):len(outputs)]

history2 = model2.fit(x_train, y_train, epochs=epochs, batch_size=batch_size, validation_data=(x_validate, y_validate))

# plot the accuracy and error for model with dropout layer
# epoch_list = np.linspace(1,500, 500)
# plt.plot(epoch_list, history2.history['acc'], 'b-')
# plt.plot(epoch_list, history2.history['val_acc'], 'r-')
#
# plt.xlabel('Epoch Number')
# plt.ylabel('Accuracy')
# plt.title('Model 2 Accuracy')
# plt.legend(['Training Accuracy', 'Validation Accuracy'])
# # plt.savefig('m2acc_hiddenlayers{}.png'.format(hidden_layers))
# plt.show()
#
# plt.plot(epoch_list, history2.history['mean_squared_error'], 'b-')
# plt.plot(epoch_list, history2.history['val_mean_squared_error'], 'r-')
#
# plt.xlabel('Epoch Number')
# plt.ylabel('Mean Squared Error')
# plt.title('Model 2 Error')
# plt.legend(['Training Error', 'Validation Error'])
# # plt.savefig('m2err_hiddenlayers{}_batch{}.png'.format(hidden_layers, batch_size))
# plt.show()


# predict test set values and make confusion matrix
predictions2 = model2.predict(x_test)
y_pred2 = predictions2

conf_mat2 = confusion_matrix(y_test.argmax(axis=1), y_pred.argmax(axis=1))

# plot the confusion matrix
# plt.matshow(conf_mat2, cmap=mpl.cm.binary)
# plt.xlabel('Actual')
# plt.ylabel('Predictions')
# plt.title('Model 2 Confusion Matrix')
# # plt.savefig('m2confmat_hiddenlayers{}_batch{}.png'.format(hidden_layers, batch_size))
# plt.show()

# plt.imshow(x_test[11].reshape(28,28), cmap=mpl.cm.binary)
print(y_test[11], y_pred[11])


batch_size = 32
nodes = 50
epochs = 500
learn_rate = 0.001
hidden_layers = 10


# model to take in for cross validation
model3 = Sequential()
model3.add(Dense(nodes, activation='relu', input_shape=images_flatten[0].shape))



for i in range(0, hidden_layers):
    
    model3.add(Dense(nodes, activation='relu'))
    
model3.add(Dense(len(outputs[0]), activation='softmax'))

sgd = keras.optimizers.SGD(lr=learn_rate, momentum=0.5, nesterov=False)

model3.compile(loss='categorical_crossentropy', optimizer=sgd, metrics=['accuracy', 'mse'])


# my cross validation function
def cross_validation_score(model=model3, x=images_flatten, y=outputs):
    
    size = len(x)
    # split the data into 3 unique folds
    #          train                          test                           validate
    splits = [[0, int(0.6*size),             int(0.6*size), int(0.8*size),   int(0.8*size), size],
              [int(0.2*size), int(0.8*size), int(0.8*size), size,            0, int(0.2*size)],
              [int(0.4*size), size,          0, int(0.2*size),               int(0.2*size), int(0.4*size)]]
    
    acc= []

    for split in splits:

        # clone the model and train it 3 times and get accuracy each time
        m = keras.models.clone_model(model)
        sgd = keras.optimizers.SGD(lr=learn_rate, momentum=0.5, nesterov=False)
        m.compile(loss='categorical_crossentropy', optimizer=sgd, metrics=['accuracy', 'mse'])
        
        x_train_fold = x[split[0]:split[1]]
        x_test_fold = x[split[2]:split[3]]
        x_val_fold = x[split[4]:split[5]]
        
        y_train_fold = y[split[0]:split[1]]
        y_test_fold = y[split[2]:split[3]]
        y_val_fold = y[split[4]:split[5]]
        
        history = m.fit(x_train_fold, y_train_fold, 
                        epochs=epochs, batch_size=batch_size, 
                        validation_data=(x_val_fold, y_val_fold))
        
        acc.append([history.history['acc'], history.history['val_acc']])
        
    return acc

# get the accuracy values at each epoch for the cross validation function and plot them

cross_score = cross_validation_score()

# plot the validation function results

# epoch_list = np.linspace(1,500, 500)
#
# for i in cross_score:
#
#     plt.plot(epoch_list, i[0])
#     plt.plot(epoch_list, i[1])
#
# plt.xlabel('Epoch Number')
# plt.ylabel('Accuracy')
# plt.title('Cross Validation Accuracy w/ Dropout {} Hidden Layers \n{} Batch Size'.format(hidden_layers, batch_size))
# plt.legend(['Train Accuracy, k = 1', 'Validation Accuracy, k = 1',
#            'Train Accuracy, k = 2', 'Validation Accuracy, k = 2',
#            'Train Accuracy, k = 3', 'Validation Accuracy, k = 3'])
# # plt.savefig('cross_val2_hidden_layers{}_batchsize{}.png'.format(hidden_layers, batch_size))
# plt.show()

batch_size = 512
nodes = 50
epochs = 500
learn_rate = 0.001
hidden_layers = 10
size = 6500

# model for my expierments changes the above parameters
start = time.time()

modele = Sequential()
modele.add(Dense(nodes, activation='relu', input_shape=images_flatten[0].shape))

for i in range(0, hidden_layers):
    
    modele.add(Dense(nodes, activation='relu'))
    
modele.add(Dense(len(outputs[0]), activation='softmax'))

sgd = keras.optimizers.SGD(lr=learn_rate, momentum=0.5, nesterov=False)

modele.compile(loss='categorical_crossentropy', optimizer=sgd, metrics=['accuracy', 'mse'])


i = images_flatten[:size]
o = outputs[:size]

x_train = i[:int(0.6*len(i))]
x_test = i[int(0.6*len(i)):int(0.8*len(i))]
x_validate = i[int(0.8*len(i)):len(i)]

y_train = o[:int(0.6*len(o))]
y_test = o[int(0.6*len(o)):int(0.8*len(o))]
y_validate = o[int(0.8*len(o)):len(o)]

h = modele.fit(x_train, y_train, epochs=epochs, batch_size=batch_size, validation_data=(x_validate, y_validate))


print(time.time() - start)
print(h.history['val_acc'][len(h.history['val_acc']) - 1])

