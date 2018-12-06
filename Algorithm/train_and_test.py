import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn import preprocessing
from sklearn.utils import shuffle
from sklearn.neural_network import MLPClassifier
from sklearn.externals import joblib

from keras.models import Model
from keras.layers import Dense, Flatten
from keras.layers import Conv1D, Embedding
from keras.models import Sequential
from keras.models import model_from_json  

MODEL_NUMBER=5
SUBSET_SIZE=0.8

def train(projectName):
    print(projectName)
    df=pd.read_csv("./Data/data.csv",encoding='ISO-8859-1')

    
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

        x=preprocessing.scale(x,axis=1,with_mean=True,with_std=True,copy=True)

        
        
        clf=MLPClassifier(hidden_layer_sizes=(64,32,16,8,4), max_iter=300)
        
        #clf=RandomForestClassifier(n_estimators=100)

        clf.fit(x,y)

        #save model
        joblib.dump(clf,"D:/Longmethod/model/"+projectName+"_"+str(i)+".joblib")
        
        
        models.append(clf)

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
    df=pd.read_csv("./Data/data.csv",encoding='ISO-8859-1')

    df=df[(df.projectName==projectName)]
    predicts=[]
    for i in range(MODEL_NUMBER):
        clf=models[i]
        x=df.iloc[:,4:13]
        
        x=preprocessing.scale(x,axis=1,with_mean=True,with_std=True,copy=True)

        predict=clf.predict(x)
        predicts.append(predict)

    result=[]
    for i in range(len(predicts[0])):
        total=0
        for j in range(MODEL_NUMBER):
            total=total+predicts[j][i]
        if total>=4:
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
        clf=model_from_json(open('D:/Longmethod/model/'+projectName+"_"+str(i)+'.json').read())
        clf.load_weights('D:/Longmethod/model/'+projectName+"_"+str(i)+'.h5')
        models.append(clf)

    return models


projects = ['areca-7.4.7','freeplane-1.3.12','jedit','jexcelapi_2_6_12','junit-4.10','pmd-5.2.0','weka']
ttp,ttn,tfp,tfn=0,0,0,0
for i in range(7):
    print("------------------------------------")
    models=train(projects[i])
    #models=load_models(projects[i])
    tp,tn,fp,fn=test(models,projects[i])
    ttp=ttp+tp
    ttn=ttn+tn
    tfp=tfp+fp
    tfn=tfn+fn
    eval(tp,tn,fp,fn)
print("------------------------------------")
print("Final Evaluation:")
ans=eval(ttp,ttn,tfp,tfn)

