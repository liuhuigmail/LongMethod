import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn import preprocessing
from sklearn.utils import shuffle
from sklearn.neural_network import MLPClassifier
from sklearn import neighbors
from sklearn import svm
from sklearn.externals import joblib

from keras.models import Model
from keras.layers import Dense, Flatten
from keras.layers import Conv1D, Embedding
from keras.models import Sequential
from keras.models import model_from_json  
from keras.layers import merge,pooling

MODEL_NUMBER=5
SUBSET_SIZE=0.8

def train(projectName):
    print(projectName)
    df=pd.read_csv("D:/Longmethod/data_semi.csv",encoding='ISO-8859-1')

    
    df=df[(df.projectName!=projectName)]


    data0=df[(df.Label==0)]
    data1=df[(df.Label==1)]

    num=(int)(data1.shape[0]*SUBSET_SIZE)
    
    models=[]
    for i in range(MODEL_NUMBER):
        print("training ",i+1,"th model")
        data0=shuffle(data0)
        data1=shuffle(data1)

        train_set0=data0.iloc[:num,:]
        train_set1=data1.iloc[:num,:]

        data=train_set0.append(train_set1)
        data=shuffle(data)

        x=data.iloc[:,4:13]
        y=np.array(data.iloc[:,13])


        conv1=Sequential()
        conv2=Sequential()
        conv3=Sequential()
        conv4=Sequential()

        conv1.add(Conv1D(64,1,padding="same",input_shape=(9,1),activation="relu"))
        conv1.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv1.add(Conv1D(64,1,padding="same",activation="relu"))
        conv1.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv1.add(Conv1D(64,1,padding="same",activation="relu"))
        conv1.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv1.add(Flatten())

        conv2.add(Conv1D(64,2,padding="same",input_shape=(9,1),activation="relu"))
        conv2.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv2.add(Conv1D(64,2,padding="same",activation="relu"))
        conv2.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv2.add(Conv1D(64,2,padding="same",activation="relu"))
        conv2.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv2.add(Flatten())

        conv3.add(Conv1D(64,3,padding="same",input_shape=(9,1),activation="relu"))
        conv3.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv3.add(Conv1D(64,3,padding="same",activation="relu"))
        conv3.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv3.add(Conv1D(64,3,padding="same",activation="relu"))
        conv3.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv3.add(Flatten())

        conv4.add(Conv1D(64,4,padding="same",input_shape=(9,1),activation="relu"))
        conv4.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv4.add(Conv1D(64,4,padding="same",activation="relu"))
        conv4.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv4.add(Conv1D(64,4,padding="same",activation="relu"))
        conv4.add(pooling.MaxPooling1D(pool_size=2,padding="same"))
        conv4.add(Flatten())

        output=merge.Concatenate()([conv1.output,conv2.output,conv3.output,conv4.output])

        output=Dense(128, activation='relu')(output)
        output=Dense(2,activation='sigmoid')(output)

        input1=conv1.input
        input2=conv2.input
        input3=conv3.input
        input4=conv4.input

        model=Model([input1,input2,input3,input4],output)

        model.compile(loss='binary_crossentropy',optimizer='Adadelta',metrics=['accuracy'])

        ty=[]
        for j in range(len(y)):
            if y[j]==1:
                ty.append([0,1])
            else:
                ty.append([1,0])
        x=np.expand_dims(x,axis=2)
        model.fit([x,x,x,x],np.array(ty),epochs=6,verbose=0)

        #save model
        json_string = model.to_json()
        open('D:/Longmethod/model/cnn/'+projectName+"_"+str(i)+'.json','w').write(json_string)
        model.save_weights('D:/Longmethod/model/cnn/'+projectName+"_"+str(i)+'.h5')
      
        models.append(model)
    return models

def eval(tp,tn,fp,fn):
    print("tp : ",tp)
    print("tn : ",tn)
    print("fp : ",fp)
    print("fn : ",fn)
    P=tp*1.0/(tp+fp)
    R=tp*1.0/(tp+fn)
    print("Precision : ",P)
    print("Recall : ",R)
    print("F1 : ",2*P*R/(P+R))
    return 2*P*R/(P+R)

def test(models,projectName):
    print(projectName)
    df=pd.read_csv("D:/Longmethod/data_semi.csv",encoding='ISO-8859-1')

    df=df[(df.projectName==projectName)]
    predicts=[]
    for i in range(MODEL_NUMBER):
        clf=models[i]
        x=np.array(df.iloc[:,4:13])
        
        #x=preprocessing.scale(x,axis=1,with_mean=True,with_std=True,copy=True)
        x=np.expand_dims(x,axis=2)
        predict=np.argmax(clf.predict([x,x,x,x]),axis=1)
        predicts.append(predict)

    result=[]
    for i in range(len(predicts[0])):
        total=0
        for j in range(MODEL_NUMBER):
            total=total+predicts[j][i]
        if total>=3:
            result.append(1)
        else:
            result.append(0)

    y=np.array(df.iloc[:,13])

    tp,tn,fp,fn=0,0,0,0

    for i in range(len(y)):
        if result[i]==y[i]:
            if result[i]==0:
                tn=tn+1
            else:
                tp=tp+1
        else:
            if result[i]==0:
                fn=fn+1
            else:
                fp=fp+1

    return tp,tn,fp,fn

def load_models(projectName):
    models=[]

    for i in range(MODEL_NUMBER):
        clf=model_from_json(open('D:/Longmethod/model/cnn/'+projectName+"_"+str(i)+'.json').read())
        clf.load_weights('D:/Longmethod/model/cnn/'+projectName+"_"+str(i)+'.h5')
        models.append(clf)

    return models


projects = ['areca-7.4.7','freeplane-1.3.12','jedit','jexcelapi_2_6_12','junit-4.10','pmd-5.2.0','weka']

ttp,ttn,tfp,tfn=0,0,0,0
for i in range(7):
    print("------------------------------------")
    #models=train(projects[i])
    models=load_models(projects[i])
    tp,tn,fp,fn=test(models,projects[i])
    ttp=ttp+tp
    ttn=ttn+tn
    tfp=tfp+fp
    tfn=tfn+fn
    eval(tp,tn,fp,fn)
print("------------------------------------")
print("Final Evaluation:")
ans=eval(ttp,ttn,tfp,tfn)

