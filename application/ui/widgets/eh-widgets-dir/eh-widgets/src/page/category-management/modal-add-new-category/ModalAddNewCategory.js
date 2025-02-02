import {Button, Modal} from "carbon-components-react"
import {Add16} from '@carbon/icons-react'
import ReactDOM from "react-dom"
import {useState} from "react"
import NewCategory from "./new-category/NewCategory"
import {addNewCategory} from "../../../integration/Integration"
import "./modal-add-new-category.scss"
import { categorySchema } from "../../../helpers/validation/categorySchema"
import { fillErrors } from "../../../helpers/validation/fillErrors"
export const ModalAddNewCategory = ({onAfterSubmit}) => {


    const ModalStateManager = ({
                                   renderLauncher: LauncherContent,
                                   children: ModalContent,
                               }) => {
        const [open, setOpen] = useState(false)
        const [elemKey, setElemKey] = useState(((new Date()).getTime()).toString()) //to clear form data
        const [category, setCategory] = useState({})
        const [validationResult, setValidationResult] = useState({})

        const onDataChange = (newCategory)=>{
            setCategory(newCategory)
        }


        const onRequestClose = (e) =>{
            resetData()
            setOpen(false)
        }

        const onRequestOpen = (e) =>{
            setOpen(true)
        }

        const resetData = ()=>{
            setElemKey(((new Date()).getTime()).toString())
        }

        //Manage the modal submit
        const onRequestSubmit = (e) => {
            (async () => {
                let validationError
                await categorySchema.validate(category, {abortEarly: false}).catch(error => {
                    validationError = fillErrors(error)
                })
                if (validationError) {
                    setValidationResult(validationError)
                    return //don't send the form
                }
                await addNewCategory(category)
                onRequestClose()
                onAfterSubmit()
            })()
        }

        return (
            <>
                {!ModalContent || typeof document === 'undefined'
                    ? null
                    : ReactDOM.createPortal(
                        <ModalContent validationResult={validationResult} open={open} onRequestClose={onRequestClose} onDataChange={onDataChange} onRequestSubmit={onRequestSubmit} elemKey={elemKey}/>,
                        document.body
                    )}
                {LauncherContent && <LauncherContent onRequestOpen={onRequestOpen}/>}
            </>
        )
    }





    return (
        <ModalStateManager
            renderLauncher={({onRequestOpen}) => (
                <Button onClick={onRequestOpen} renderIcon={Add16}>Add Category</Button>
            )}>
            {({open, onRequestClose, onDataChange, onRequestSubmit, elemKey, validationResult}) => (
                <Modal
                    modalLabel="Add"
                    className="Modal-Add-New-organization"
                    primaryButtonText="Add"
                    secondaryButtonText="Cancel"
                    open={open}
                    onRequestClose={onRequestClose}
                    onRequestSubmit={onRequestSubmit}>
                    <NewCategory key={elemKey} onDataChange={onDataChange} validationResult={validationResult}/>
                </Modal>
            )}
        </ModalStateManager>
    )
}
