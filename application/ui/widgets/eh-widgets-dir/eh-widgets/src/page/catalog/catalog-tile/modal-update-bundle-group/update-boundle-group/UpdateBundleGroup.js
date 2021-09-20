import {useCallback, useEffect, useState} from "react";
import {Content, Select, SelectItem, TextArea, TextInput,} from "carbon-components-react";
import {
    getAllBundlesForABundleGroup,
    getAllCategories,
    getSingleBundleGroup
} from "../../../../../integration/Integration";
import BundlesOfBundleGroup from "./bundles-of-bundle-group/BundlesOfBundleGroup";
import {getProfiledUpdateSelectStatusInfo} from "../../../../../helpers/profiling";
import {getHigherRole} from "../../../../../helpers/helpers";

/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}
 */

const UpdateBundleGroup = ({bundleGroupId, onDataChange}) => {

    const [selectOptions, setSelectOptions] = useState([]);
    const [selectDisabled, setSelectDisabled] = useState(false);
    const [children, setChildren] = useState([]);
    const [categories, setCategories] = useState([]);
    const [bundleGroup, setBundleGroup] = useState({
        name: "",
        description: "",
        descriptionImage: "",
        documentationUrl: "",
        status: "",
        children: [],
        categories: [],
    });

    const changeBundleGroup = (field, value) => {
        const newObj = {
            ...bundleGroup,
        }
        newObj[field] = value
        setBundleGroup(newObj)
        onDataChange(newObj)
    }

    const createSelectOptionsForRoleAndSetSelectStatus = useCallback((bundleGroup) => {
        const selectValuesInfo = getProfiledUpdateSelectStatusInfo(getHigherRole(), bundleGroup.status)
        setSelectDisabled(selectValuesInfo.disabled)
        const options = selectValuesInfo.values.map((curr, index) => <SelectItem key={index} value={curr.value} text={curr.text}/>)
        setSelectOptions(options)
    }, [])


    useEffect(() => {
        let isMounted = true;
        const initCG = async () => {
            const res = await getAllCategories();
            if (isMounted) {
                setCategories(res.categoryList);
            }
        }
        const initBG = async () => {
            const res = await getSingleBundleGroup(bundleGroupId);

            const childrenFromDb = res.bundleGroup.children && res.bundleGroup.children.length > 0
                ? (await getAllBundlesForABundleGroup(bundleGroupId)).bundleList
                : []

            if (isMounted) {
                let bg = {
                    ...res.bundleGroup,
                    children: childrenFromDb
                }
                setBundleGroup(bg);
                setChildren(childrenFromDb)
                onDataChange(bg)
                createSelectOptionsForRoleAndSetSelectStatus(bg)
            }
        }
        initCG()
        initBG()
        return () => {
            isMounted = false
        }

    }, [bundleGroupId, onDataChange, createSelectOptionsForRoleAndSetSelectStatus]);

    let selectItems_Category = categories.map((category) => {
        return (
            <SelectItem
                key={category.categoryId}
                value={category.categoryId}
                text={category.name}
            />
        );
    });


    const nameChangeHandler = (e) => {
        changeBundleGroup("name", e.target.value)
    }

    const categoryChangeHandler = (e) => {
        changeBundleGroup("categories", [e.target.value])
    }

    const documentationChangeHandler = (e) => {
        changeBundleGroup("documentationUrl", e.target.value)
    }

    const versionChangeHandler = (e) => {
        // const value = e.target.value;
        // setNewBundleGroup(prev => {
        //   return {
        //     ...prev,
        //     version: value
        //   }
        // })
        //changeNewBundleGroup("version", e.target.value)
    }

    const statusChangeHandler = (e) => {
        changeBundleGroup("status", e.target.value)
    }

    const descriptionChangeHandler = (e) => {
        changeBundleGroup("description", e.target.value)
    }

    const onAddOrRemoveBundleFromList = (newBundleList) => {
        changeBundleGroup("children", newBundleList)
    }

    console.log("UBG", bundleGroup)

    return (
        <>
            <Content>
                <TextInput value={bundleGroup.name} onChange={nameChangeHandler} id={"name"} labelText={"Name"}/>
                <Select value={bundleGroup.categories[0]} onChange={categoryChangeHandler} id={"category"}
                        labelText={"Category"}>{selectItems_Category}</Select>
                <TextInput value={bundleGroup.documentationUrl} onChange={documentationChangeHandler}
                           id={"documentation"}
                           labelText={"Documentation Address"}/>
                <TextInput value={bundleGroup.version} onChange={versionChangeHandler} id={"version"}
                           labelText={"Version"}/>
                <Select disabled={selectDisabled} value={bundleGroup.status} onChange={statusChangeHandler}
                        id={"status"}
                        labelText={"Status"}>{selectOptions}</Select>
                <TextArea value={bundleGroup.description} onChange={descriptionChangeHandler} id={"description"}
                          labelText={"Description"}/>
                <BundlesOfBundleGroup onAddOrRemoveBundleFromList={onAddOrRemoveBundleFromList}
                                      initialBundleList={children}/>
            </Content>
        </>
    );
};

export default UpdateBundleGroup;
