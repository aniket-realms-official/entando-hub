import "./index.scss"
import "./App-carbon.scss"
import {HashRouter, Route, Switch} from "react-router-dom"
import CatalogPage from "./page/catalog/CatalogPage"
import BundleGroupPage from "./page/bundle-group/BundleGroupPage"
import UserManagementPage from "./page/user-management/UserManagementPage"
import OrganisationManagementPage from "./page/organisation-management/OrganisationManagementPage"
import CategoryManagementPage from "./page/category-management/CategoryManagementPage"
import RouteWithGate from "./components/routing/RouteWithGate";
import {isHubAdmin} from "./helpers/helpers";
import NotificationDispatcher from "./components/notification/NotificationDispatcher"

function AppCarbon() {
  return (
    <>
      <NotificationDispatcher />
        <HashRouter>
            <Switch>
                <Route path="**/bundlegroup/:id" component={BundleGroupPage}/>
                <RouteWithGate gateFunction={isHubAdmin} path="**/admin*" component={UserManagementPage}/>
                <RouteWithGate gateFunction={isHubAdmin} path="**/organisations*" component={OrganisationManagementPage}/>
                <RouteWithGate gateFunction={isHubAdmin} path="**/organisation*" component={OrganisationManagementPage}/>
                <RouteWithGate gateFunction={isHubAdmin} path="**/categories*" component={CategoryManagementPage}/>
                <RouteWithGate gateFunction={isHubAdmin} path="**/category*" component={CategoryManagementPage}/>
                <Route path="**/unauthorized">
                    UNAUTHORIZED
                </Route>
                <Route path="**/" component={CatalogPage}/>
            </Switch>
        </HashRouter>
    </>
  )
}

export default AppCarbon
