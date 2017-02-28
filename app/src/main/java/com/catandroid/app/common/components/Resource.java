package com.catandroid.app.common.components;

import com.catandroid.app.R;

/**
 * Created by logan on 2017-02-27.
 */

public class Resource {

    public static final ResourceType[] RESOURCE_TYPES =
            { ResourceType.LUMBER, ResourceType.WOOL, ResourceType.GRAIN,
                    ResourceType.BRICK, ResourceType.ORE };

    private ResourceType resourceType;

    public Resource(ResourceType resourceResourceType) {
        this.resourceType = resourceResourceType;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public enum ResourceType {
        LUMBER, WOOL, GRAIN, BRICK, ORE, ANY
    }

    public int toResourceIndex() {
        return toResourceIndex(this.resourceType);
    }

    public int toRString() {
        return toRString(this.resourceType);
    }

    public static int toResourceIndex(ResourceType resourceType) {
        switch(resourceType) {
            case LUMBER:
                return 0;
            case WOOL:
                return 1;
            case GRAIN:
                return 2;
            case BRICK:
                return 3;
            case ORE:
                return 4;
            default:
                return -1;
        }
    }

    public static int toRString(ResourceType resourceType) {
        switch (resourceType) {
            case LUMBER:
                return R.string.lumber;
            case WOOL:
                return R.string.wool;
            case GRAIN:
                return R.string.grain;
            case BRICK:
                return R.string.brick;
            case ORE:
                return R.string.ore;
            default:
                return R.string.nostring;
        }
    }
}
