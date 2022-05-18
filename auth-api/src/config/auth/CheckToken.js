import jwt from 'jsonwebtoken';
import { promisify } from 'util';

import * as secrets from '../../config/constants/Secrets.js';
import * as httpStatus from '../../config/constants/HttpStatus.js';

import AccessException from './AuthException.js';

const bearer = 'bearer';

export default async (req, res, next) => {
    console.log('Filter')
    try {
        const { authorization } = req.headers;
        if (!authorization) {
            throw new AccessException(httpStatus.UNAUTHORIZED,
                "Access token was not informed.")
        }

        let accessToken = authorization;
        console.log(accessToken)
        if (accessToken.includes(bearer)) {
            accessToken = accessToken.split(" ")[1]
        }
        console.log(accessToken)

        const decoded = await promisify(jwt.verify)(
            accessToken,
            secrets.API_SECRET
        )

        console.log(decoded.authUser)
        req.authUser = decoded.authUser;

        return next();
    } catch (error) {
        const status = error.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR
        return res.status(status).json({
            status: status,
            message: error.message
        })
    }



}