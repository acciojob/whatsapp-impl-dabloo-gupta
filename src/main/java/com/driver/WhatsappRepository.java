package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 1;
        this.messageId = 0;
    }

    public String  createUser(String name , String mobile) throws Exception{

        if(userMobile.contains(mobile)){
            throw new Exception("User already exist");
        }
            User u = new User(name, mobile);
            userMobile.add(mobile);
            return "SUCCESS";
    }
    public Group createGroup(List<User> users){
        Group g = new Group();
        if(users.size()==2){
            g.setName(users.get(1).getName());
            g.setNumberOfParticipants(2);
        }else{
            g.setName("Group " + customGroupCount++ );
            g.setNumberOfParticipants(users.size());
        }
        groupUserMap.put(g,users);
        groupMessageMap.put(g,new ArrayList<>());
        adminMap.put(g,users.get(0));
        return g;
    }
    public int addMessage(String content) {
        Message m = new Message();
        messageId++;
        m.setId(messageId);
        m.setContent(content);
        m.setTimestamp(new Date());
        return m.getId();
    }
    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        List<User> users = groupUserMap.get(group);
        boolean userFound = false;
        for(User u : users) {
            if(sender.equals(u)){
                userFound = true;
                break;
            }
        }
        if(!userFound)
            throw new Exception("You are not allowed to send message");
        groupMessageMap.get(group).add(message);
        senderMap.put(message,sender);
        return groupMessageMap.get(group).size();

    }
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.

        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        List<User> users = groupUserMap.get(group);
        boolean flag = false;
        for(User u:users){
            if(user.equals(u)) {
                flag = true;
                break;
            }
        }
        if(!flag){
            throw new Exception("User is not a participant");
        }
        if(!adminMap.get(group).equals(approver)){
            throw new Exception("Approver does not have rights");
        }
        adminMap.put(group,user);
        return "SUCCESS";
    }


}
