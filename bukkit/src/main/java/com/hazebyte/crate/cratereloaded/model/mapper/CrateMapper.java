package com.hazebyte.crate.cratereloaded.model.mapper;

import com.hazebyte.crate.cratereloaded.model.CrateImpl;
import com.hazebyte.crate.cratereloaded.model.CrateV2;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {RewardMapper.class, CommonMapperUtil.class})
public interface CrateMapper {

    @Mapping(target = "displayName", qualifiedByName = "wrapOptional")
    @Mapping(target = "displayItem", qualifiedByName = "wrapOptional")
    @Mapping(source = "UUID", target = "uuid")
    @Mapping(source = "cost", target = "salePrice")
    @Mapping(source = "buyable", target = "forSale")
    @Mapping(target = "rewards", qualifiedByName = "toRewardListV2")
    @Mapping(target = "constantRewards", qualifiedByName = "toRewardListV2")
    CrateV2 fromImplementation(CrateImpl crate);

    @Mapping(source = "crateName", target = "name")
    @Mapping(source = "salePrice", target = "cost")
    @Mapping(source = "forSale", target = "buyable")
    @Mapping(target = "displayItem", qualifiedByName = "unwrap")
    @Mapping(target = "displayName", qualifiedByName = "unwrap")
    CrateImpl toImplementation(CrateV2 crateV2);

    @BeforeMapping
    default void setManually(CrateV2 source, @MappingTarget CrateImpl target) {
        target.setAnimationType(source.getAnimationType());
        target.setConfirmationToggle(source.isConfirmBeforeUse());
        target.setAcceptButton(source.getAcceptButton());
        target.setDeclineButton(source.getDeclineButton());
    }
}
