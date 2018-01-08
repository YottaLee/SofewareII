package server.Dataservice.Goodsdataservice;

import server.Dataservice.Pubservice.pub;
import shared.ResultMessage;

import java.rmi.RemoteException;
import java.util.List;

public interface GoodsKinds extends pub {
    ResultMessage addObject(Object object,int type) throws RemoteException;
    ResultMessage deleteObject(Object object,int type) throws RemoteException;
    ResultMessage modifyObject(Object object,int type) throws RemoteException;
    List goodsKindsFind(String keyword)throws RemoteException;
}
