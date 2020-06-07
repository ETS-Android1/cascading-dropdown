package com.sample.tanay.dynamicspinner;

import android.util.SparseIntArray;

import androidx.annotation.NonNull;

import java.util.ArrayList;

final class DataNode {

    static void populateLeafNodes(ArrayList<DataNode> leafNodes, DataNode dataNode, int maxLevel,
                                  int currentLevel) {
        boolean isPenultimateLevel = (currentLevel + 1) == maxLevel;
        if (isPenultimateLevel) {
            leafNodes.addAll(dataNode.children);
        } else {
            if (dataNode.children != null) {
                for (DataNode child : dataNode.children) {
                    populateLeafNodes(leafNodes, child, maxLevel, currentLevel + 1);
                }
            }
        }
    }

    static int getPosition(DataNode dataNode, ArrayList<DataNode> dataNodes) {
        return getPosition(dataNode.name, dataNodes);
    }

    static int getPosition(String name, ArrayList<DataNode> dataNodes) {
        int pos = -1, index = 0;
        for (DataNode dataNode1 : dataNodes) {
            if (dataNode1.name.equals(name)) {
                pos = index;
                break;
            }
            index++;
        }
        return pos;
    }


    public String name;

    Integer id;

    Integer parentId;

    transient DataNode parent;

    ArrayList<DataNode> children;

    DataNode(String name) {
        this.name = name;
    }

    DataNode getChild(String name) {
        if (children != null) {
            for (DataNode dataNode : children) {
                if (dataNode.name.equalsIgnoreCase(name)) {
                    return dataNode;
                }
            }
        }
        return null;
    }

    DataNode getChild(int id) {
        if (children != null) {
            for (DataNode dataNode : children) {
                if (dataNode.id == id) {
                    return dataNode;
                }
            }
        }
        return null;
    }

    DataNode(String name, int id) {
        this.name = name;
        this.id = id;
    }


    void setAsParent(int currentLevel, int maxLevel) {
        if (children != null) {
            boolean notPenultimateLevel = currentLevel + 1 < maxLevel;
            for (DataNode dataNode : children) {
                dataNode.parent = this;
                if (notPenultimateLevel) {
                    dataNode.setAsParent(currentLevel + 1, maxLevel);
                }
            }
        }
    }


    void assignParentId(boolean assignId) {
        if (children != null)
            for (DataNode dataNode : children) {
                if (assignId)
                    dataNode.parentId = id;
                dataNode.assignParentId(true);
            }
    }

    void assignId(int level, SparseIntArray levelToIdMap) {
        int id = (levelToIdMap.get(level) == 0) ? 1 : levelToIdMap.get(level) + 1;
        levelToIdMap.put(level, id);
        this.id = id;
        level = level + 1;
        if (children != null)
            for (DataNode child : children) {
                child.assignId(level, levelToIdMap);
            }
    }


    String getSuggestionText() {
        StringBuilder stringBuilder = new StringBuilder();
        DataNode parentCopy = parent;
        stringBuilder.append(name).append(",");
        while (parentCopy != null && parentCopy.parent != null) {
            stringBuilder.append(' ').append(parentCopy.name).append(",");
            parentCopy = parentCopy.parent;
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
