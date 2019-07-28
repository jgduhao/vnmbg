package consts

interface ErrTypes {

    def errWithCode = [
            'fieldEmpty' : 400,
            'resExists' : 400,
            'resNotFound' : 404
    ]

}