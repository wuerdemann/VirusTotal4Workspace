/**
 * 
 */
package net.miwu.virustotal.client;

import net.miwu.virustotal.model.VTResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author miwu
 *
 */
public interface VirusTotalClient {

	@POST("https://www.virustotal.com/vtapi/v2/file/report")
	@FormUrlEncoded
	Call<VTResponse> getVirusTotalReport(@Field("apikey") String apiKey, @Field("resource") String fileHash);
}
