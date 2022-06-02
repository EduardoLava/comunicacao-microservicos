import axios from "axios";
import { PRODUCT_API_URL } from "../../../../config/constants/Secrets.js";

class ProductClient {

    async checkProductStock(productsData, token, transactionid) {
        try {
            const headers = {
                Authorization: token,
                transactionid
            }
            var body = { products: productsData.products };
            console.info(`Request to PRODUCT API with data ${JSON.stringify(body)} | [transactionid: ${transactionid}`)
            let response = false;
            await axios.post(
                `${PRODUCT_API_URL}/check-stock`,
                body,
                { headers },
            ).then(res => {
                console.info(`Success response from PRODUCT API | [transactionid: ${transactionid}`)
                response = true;
            }).catch(err => {
                console.error(`Error response from PRODUCT API | [transactionid: ${transactionid}`)
                console.error(err.response.message);
                response = false;
            })
            return response;
        } catch (error) {
            console.error(`Error response from PRODUCT API | [transactionid: ${transactionid}`)
            console.log(`Error while check products stock ${error}`);
            return false;
        }
    }
}

export default new ProductClient();