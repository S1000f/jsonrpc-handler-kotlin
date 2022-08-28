package dispatcher

import dto.ContextHolder

enum class Specification : ContextBuilder {

    V1_0 {
        override fun build(jsonHolder: JsonHolder?): ContextHolder? {
            TODO("Not yet implemented")
        }
    },

    V2_0 {
        override fun build(jsonHolder: JsonHolder?): ContextHolder? {




            TODO("Not yet implemented")
        }
    };

    abstract override fun build(jsonHolder: JsonHolder?): ContextHolder?
}