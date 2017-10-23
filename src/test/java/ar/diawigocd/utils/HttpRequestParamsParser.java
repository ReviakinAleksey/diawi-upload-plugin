package ar.diawigocd.utils;

import org.apache.http.HttpEntity;
import org.apache.http.RequestLine;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRequestParamsParser {

    private static final Pattern CONTENT_DISPOSITION = Pattern.compile("Content-Disposition: form-data; name=\"(?<name>.+?)\"(; filename=\"(?<fileName>.+?)\")?");
    private static final Pattern CONTENT_TYPE = Pattern.compile("Content-Type: (?<type>.+)");


    public static Stream<HttpRequestParam> partsFromContent(String content) {
        Optional<String> tokenOption = Pattern.compile("\r\n").splitAsStream(content).findFirst();

        if (tokenOption.isPresent()) {
            String formToken = tokenOption.get();

            String innerContent = Pattern
                    .compile("\r\n" + Pattern.quote(formToken) + "--\r\n")
                    .matcher(content).replaceFirst("");

            return Pattern.compile("\r\n" + Pattern.quote(formToken) + "\r\n", Pattern.MULTILINE)
                    .splitAsStream("\r\n" + innerContent)
                    .skip(1)
                    .map(part -> {
                        String[] partArray = part.split(Pattern.quote("\r\n\r\n"), 2);
                        String partHeaders = partArray[0];
                        String partContent = partArray[1];
                        HttpRequestParam httpRequestParam = new HttpRequestParam();
                        Stream.of(partHeaders.split("\r\n")).forEach(s -> {
                            Matcher matcher = CONTENT_DISPOSITION.matcher(s);
                            if (matcher.find()) {
                                httpRequestParam.setName(matcher.group("name"));
                                httpRequestParam.setFileName(matcher.group("fileName"));
                            }
                            Matcher typeMather = CONTENT_TYPE.matcher(s);
                            if (typeMather.find()) {
                                httpRequestParam.setContentType(typeMather.group("type"));
                            }
                        });
                        httpRequestParam.setContent(partContent);
                        return httpRequestParam;
                    });


        } else {
            return Stream.empty();
        }
    }

    public static Stream<HttpRequestParam> parseGetParams(RequestLine line) throws IOException {
        String uri = line.getUri();
        String[] split = uri.split("\\?", 2);
        if (split.length > 1) {
            return URLEncodedUtils
                    .parse(split[1], StandardCharsets.UTF_8)
                    .stream()
                    .map(nameValuePair -> {
                        HttpRequestParam httpRequestParam = new HttpRequestParam();
                        httpRequestParam.setName(nameValuePair.getName());
                        httpRequestParam.setContent(nameValuePair.getValue());
                        return httpRequestParam;
                    });

        } else {
            return Stream.empty();
        }

    }

    public static Map<String, HttpRequestParam> parseGetToMap(RequestLine line) throws IOException {
        return parseGetParams(line).collect(toParamsMap);
    }


    public static Collector<HttpRequestParam, ?, Map<String, HttpRequestParam>> toParamsMap = Collectors.toMap(HttpRequestParam::getName, Function.identity());


    public static Map<String, HttpRequestParam> parsePostEntity(HttpEntity entity) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        entity.writeTo(bos);
        String content = new String(bos.toByteArray(), StandardCharsets.UTF_8);
        return partsFromContent(content).collect(toParamsMap);
    }


}
