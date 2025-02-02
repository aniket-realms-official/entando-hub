import {getAllUsers, getSingleOrganisation} from "./Integration"
import {getUserName, isHubUser} from "../helpers/helpers";

//portal user
export const getPortalUserDetails = async (username) => {
    const userList = (await getAllUsers()).userList
    if (userList && userList.length) {
        const filteredUserList = userList.filter(user => user.username === username)
        if (filteredUserList.length > 0) {
            const user = filteredUserList[0]
            if (user.organisationIds && user.organisationIds.length > 0) {
                const organisation = (await getSingleOrganisation(user.organisationIds[0])).organisation
                return {
                    ...user,
                    organisation
                }
            }
            return {
                ...user
            }
        }
    }
    return undefined
}



export const getCurrentUserOrganisation= async ()=>{
    //TODO cache user info
    if (isHubUser()) {
        const username = await getUserName()
        const portalUserDetail = await getPortalUserDetails(username)
        if (portalUserDetail && portalUserDetail.organisation) {
            return portalUserDetail.organisation
        }
    }

    return undefined
}
